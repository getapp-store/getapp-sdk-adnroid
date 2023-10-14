package ru.kovardin.boosty

import android.content.Context
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import ru.kovardin.utils.Dialog
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class Boosty(
    val app: String,
    val api: String,
    val context: Context,
) {
    private val client = OkHttpClient()

    fun subscriptions(handler: SubscriptionsHandler) {
        // get subscriptions by app
        val request = Request.Builder()
            .url("${api}/v1/boosty/${app}/subscriptions")
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
                    val resp: SubscriptionsResponse

                    try {
                        resp = Gson().fromJson(body, SubscriptionsResponse::class.java)
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

    fun restore() {
        // auth and load boosty page
    }

    fun subscribe() {
        // open auth window
        // check is user subscribed
        // get subscriber id

        auth(object : AuthHandler {
            override fun onFailure(e: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(resp: AuthResponse) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun auth(handler: AuthHandler) {
        // get url by app from server
        val url = "https://boosty.to/getapp"

        val dialog = Dialog(context = context, url = url)
        dialog.client = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(w: WebView, u: String): Boolean {
                w.loadUrl(u)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {

//                dialog.close()

                Log.w("Boosty", url.orEmpty())
            }
        }

        dialog.open()
    }

    companion object {
        lateinit var client: Boosty

        fun init(
            app: String,
            api: String = "https://service.getapp.store",
            context: Context,
        ) {
            client = Boosty(app = app, api = api, context = context)
        }
    }
}