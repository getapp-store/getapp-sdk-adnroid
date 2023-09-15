package ru.kovardin.getappbilling

import android.content.Context
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.kovardin.getappbilling.utils.param
import java.io.IOException
import java.net.URL

class Billing(
    val app: String,
    val api: String,
    val context: Context,
) {
    private val client = OkHttpClient()

    fun products(handler: ProductsHandler) {
        val request = Request.Builder()
            .url("${api}/v1/billing/${app}/products")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        handler.onFailure(IOException("Unexpected code $response"))
                    }

                    val body = response.body?.string().orEmpty()

                    Log.i("Billing", body)

                    var resp: ProductsResponse

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
        auth(object : AuthHandler {
            override fun onFailure(e: Throwable) {
                handler.onFailure(e)
            }

            override fun onSuccess(resp: AuthResponse) {
                val token = resp.token

                val dial = Dialog(context = context, url = "${api}/v1/billing/${app}/payments/purchase?product=${id}")
                dial.client = object : WebViewClient() {
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
                            val u = URL(url)
                            val payment = u.param("payment").orEmpty()
                            val status = u.param("status").orEmpty()
                            val product = u.param("product").orEmpty()

                            dial.close()

                            handler.onSuccess(PurchaseResponse(id = payment, status = status, product = product))
                        }
                    }
                }

                dial.open()
            }
        })
    }

    fun restore(handler: RestoreHandler) {
        // restore all purchase
        // load webview
        // track success results

        auth(object : AuthHandler {
            override fun onFailure(e: Throwable) {
                handler.onFailure(e)
            }

            override fun onSuccess(resp: AuthResponse) {
                // make dialog with purchase restore
            }

        })
    }

    private fun auth(handler: AuthHandler) {

        val dial = Dialog(context = context, url = "${api}/v1/users/${app}/choose")

        dial.client = object : WebViewClient() {
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

                    dial.close()

                    handler.onSuccess(AuthResponse(token = token))
                }

                Log.w("Billing", url.orEmpty())
            }
        }

        dial.open()
    }

    internal fun dialog() {

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