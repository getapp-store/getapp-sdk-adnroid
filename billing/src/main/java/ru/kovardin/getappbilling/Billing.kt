package ru.kovardin.getappbilling

import android.content.Context
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.kovardin.getappbilling.utils.param
import ru.kovardin.utils.Dialog
import java.io.IOException
import java.net.URL

class Billing(
    val app: String,
    val api: String,
    val context: Context,
) {
    private val client = OkHttpClient()
    private var savedToken = "";

    fun products(handler: ProductsHandler) {
        val request = Request.Builder()
            .url("${api}/v1/billing/${app}/products")
            // @todo: apitoken?
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        if (response.code == 403) {
                            savedToken = ""
                        }

                        handler.onFailure(IOException("Unexpected code $response"))
                    }

                    val body = response.body?.string().orEmpty()
                    val resp: ProductsResponse

                    try {
                        resp = Gson().fromJson(body, ProductsResponse::class.java)
                    } catch (e: Exception) {
                        Log.e("Billing", e.message.orEmpty())
                        handler.onFailure(e)
                        return
                    }

                    handler.onSuccess(resp)
                }
            }
        })
    }

    fun purchase(id: String, handler: PurchaseHandler) {
        // show auth dialog
        auth(object : AuthHandler {
            override fun onFailure(e: Throwable) {
                handler.onFailure(e)
            }

            override fun onSuccess(resp: AuthResponse) {
                val token = resp.token

                // show payment dialog
                val dialog = Dialog(context = context, url = "${api}/v1/billing/${app}/payments/purchase?product=${id}")
                dialog.client = object : WebViewClient() {
                    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                        val url = request!!.url.toString()
                        return if (url.contains("${api}/v1/billing/${app}")) {
                            try {
                                val httpClient = OkHttpClient()
                                val request: Request = Request.Builder()
                                    .url(url.trim())
                                    .addHeader("X-User-Key", "Bearer ${token}") // Example header
                                    .build()
                                val response = httpClient.newCall(request).execute()

                                if (response.code == 403) {
                                    savedToken = ""
                                    handler.onFailure(IOException("Unexpected code $response"))
                                    return null
                                }

                                WebResourceResponse(
                                    null,
                                    response.header("content-encoding", "utf-8"),
                                    response.body!!.byteStream()
                                )
                            } catch (e: Exception) {
                                Log.e("Billing", "error on load", e)
                                handler.onFailure(e)
                                return null
                            }
                        } else {
                            super.shouldInterceptRequest(view, request)
                        }
                    }

                    override fun shouldOverrideUrlLoading(w: WebView, u: String): Boolean {
                        w.loadUrl(u)
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        if (url?.contains("success?payment=") ?: false) {
                            // check payment after success window
                            val u = URL(url)
                            val payment = u.param("payment").orEmpty()

                            val request = Request.Builder()
                                .url("${api}/v1/billing/${app}/payments/${payment}")
                                .addHeader("X-User-Key", "Bearer ${token}")
                                .build()

                            client.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    handler.onFailure(e)
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    response.use {
                                        if (!response.isSuccessful) {
                                            if (response.code == 403) {
                                                savedToken = ""
                                            }

                                            handler.onFailure(IOException("Unexpected code $response"))
                                        }

                                        val body = response.body?.string().orEmpty()
                                        val resp: PurchaseResponse

                                        try {
                                            resp = Gson().fromJson(body, PurchaseResponse::class.java)
                                        } catch (e: Exception) {
                                            Log.e("Billing", e.message.orEmpty())
                                            handler.onFailure(e)
                                            return
                                        }

                                        handler.onSuccess(resp)
                                    }
                                }
                            })

                            dialog.close()
                        }
                    }
                }

                dialog.open()
            }
        })
    }

    fun restore(handler: RestoreHandler) {
        auth(object : AuthHandler {
            override fun onFailure(e: Throwable) {
                handler.onFailure(e)
            }

            override fun onSuccess(resp: AuthResponse) {
                // make dialog with purchase restore
                val token = resp.token

                val request = Request.Builder()
                    .url("${api}/v1/billing/${app}/payments/restore")
                    .addHeader("X-User-Key", "Bearer $token")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        handler.onFailure(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) {
                                if (response.code == 403) {
                                    savedToken = ""
                                }

                                handler.onFailure(IOException("Unexpected code $response"))
                            }

                            val body = response.body?.string().orEmpty()
                            val resp: RestoreResponse

                            try {
                                resp = Gson().fromJson(body, RestoreResponse::class.java)
                            } catch (e: Exception) {
                                Log.e("Billing", e.message.orEmpty())
                                handler.onFailure(e)
                                return
                            }

                            handler.onSuccess(resp)
                        }
                    }
                })
            }
        })
    }

    private fun auth(handler: AuthHandler) {
        // TODO: нужно продумать как сбрасывать токен
        // если ответ 403?
        if (savedToken != "") {
            handler.onSuccess(AuthResponse(token = savedToken))
            return
        }

        val dialog = Dialog(context = context, url = "${api}/v1/users/${app}/choose")
        dialog.client = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(w: WebView, u: String): Boolean {
                w.loadUrl(u)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (url?.contains("success?token=") ?: false) {
                    // save token for next requests
                    val token = URL(url).param("token").orEmpty()

                    dialog.close()

                    savedToken = token
                    handler.onSuccess(AuthResponse(token = token))
                }

                Log.w("Billing", url.orEmpty())
            }
        }

        dialog.open()
    }

    companion object {
        lateinit var client: Billing

        fun init(
            app: String,
            api: String = "https://service.getapp.store",
            context: Context,
        ) {
            client = Billing(app = app, api = api, context = context)
        }
    }
}