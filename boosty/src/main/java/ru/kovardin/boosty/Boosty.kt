package ru.kovardin.boosty

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.webkit.CookieManager
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
import ru.kovardin.utils.Dialog
import java.io.IOException

const val TAG = "Boosty"

class Boosty(
    val app: String,
    val api: String,
    val context: Context,
) {
    private val client = OkHttpClient()
    private var subscriber: Subscriber? = null

    fun blog(handler: BlogHandler) {
        val request = Request.Builder()
            .url("${api}/v1/boosty/${app}/blog")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        handler.onFailure(IOException("Unexpected code $response"))
                        return
                    }

                    val body = response.body?.string().orEmpty()
                    val resp: Blog

                    try {
                        resp = Gson().fromJson(body, Blog::class.java)
                    } catch (e: Exception) {
                        Log.d(TAG, e.message.orEmpty())
                        handler.onFailure(e)
                        return
                    }

                    handler.onSuccess(resp)
                }
            }
        })
    }

    fun subscriber(external: String, handler: SubscribeHandler) {
        val request = Request.Builder()
            .url("${api}/v1/boosty/${app}/subscriber/$external")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                // 5. подписку сохраняем в праметре subscriber
                if (!response.isSuccessful) {
                    return
                }

                val body = response.body?.string().orEmpty()
                val resp: Subscriber

                try {
                    resp = Gson().fromJson(body, Subscriber::class.java)
                } catch (e: Exception) {
                    Log.d(TAG, e.message.orEmpty())
                    handler.onFailure(e)
                    return
                }

               handler.onSuccess(resp)
            }
        })
    }

    fun subscriptions(handler: SubscriptionsHandler) {
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
                        Log.d(TAG, e.message.orEmpty())
                        handler.onFailure(e)
                        return
                    }

                    handler.onSuccess(resp)
                }
            }
        })
    }

    fun subscribe(external: String? = null, handler: SubscribeHandler) {
        // 0. очищаем все что мы знаем о подписчике
        subscriber = null
        // 1. получаем ссылку на блог
        blog(object : BlogHandler {
            override fun onFailure(e: Throwable) {
                handler.onFailure(e)
            }

            override fun onSuccess(resp: Blog) {
                // 2. показываем веб-вью с нужныим блогом
                (context as Activity).runOnUiThread {
                    val u = if (external.isNullOrEmpty()) {
                        resp.url
                    } else {
                        "${resp.url}/purchase/$external"
                    }

                    val dialog = Dialog(context = context, url = u)
                    dialog.setOnDismissListener {
                        // 5. при закрытии окна отправляем onSuccess если есть subscriber
                        if (subscriber == null) {
                            handler.onFailure(Exception("error on subscribe"))
                            return@setOnDismissListener
                        }

                        handler.onSuccess(subscriber!!)
                    }
                    dialog.client = object : WebViewClient() {
                        override fun shouldInterceptRequest(w: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                            val resp = super.shouldInterceptRequest(w, request)
                            return resp
                        }

                        override fun shouldOverrideUrlLoading(w: WebView, u: String): Boolean {
                            w.loadUrl(u)
                            return true
                        }

                        override fun onPageFinished(w: WebView?, u: String?) {
                            // убираем попап про скачивание приложения
                            w?.evaluateJavascript("sessionStorage.setItem(\"preventShowAppBanner\", \"true\");document.querySelectorAll('[ class^=\"NativeAppBanner_root_\" ]')[0].style.display=\"none\";", null);

                            // 3. нагло забираем куки чтоб найти ид подписчика
                            val cookies = CookieManager.getInstance().getCookie(u) ?: return
                            // https://stackoverflow.com/questions/11100086/android-extracting-cookies-after-login-in-webview

                            val user = User.parse(cookies)

                            Log.d(TAG, "external: ${user?.external()}")

                            if (user?.external()?.isBlank() == true) {
                                return
                            }

                            // 4. по полученному ид забирем подписку с бека если она есть. Подписку сохраняем в праметре subscriber
                            subscriber(user?.external() ?: "", object : SubscribeHandler {
                                override fun onFailure(e: Throwable) {
                                    // skip
                                }

                                override fun onSuccess(resp: Subscriber) {
                                    subscriber = resp
                                }

                            })
                        }
                    }
                    dialog.open()
                }
            }

        })
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var client: Boosty

        fun init(
            context: Context,
            app: String,
            api: String = "https://service.getapp.store",
        ) {
            client = Boosty(app = app, api = api, context = context)
        }
    }
}