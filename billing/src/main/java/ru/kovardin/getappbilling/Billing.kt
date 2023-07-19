package ru.kovardin.getappbilling

import android.content.Context
import android.util.Log
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
import java.io.IOException


interface ProductsHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: ProductsResponse)
}

data class Product(val id: String, val title: String, val amount: Int)

data class ProductsResponse(val items: List<Product>)

interface PurchaseHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: PurchaseResponse)
}

data class PurchaseResponse(val status: String)

class Billing(
    val token: String,
    val api: String,
    val context: Context,
) {
    private val client = OkHttpClient()

    fun products(handler: ProductsHandler) {
        val request = Request.Builder()
            .url("${api}/v1/products")
            .addHeader("Authorization", "Bearer ${token}")
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

                    val resp = Gson().fromJson(response.body!!.string(), ProductsResponse::class.java)

                    handler.onSuccess(resp)
                }
            }
        })
    }

    fun purchase(id: String, handler: PurchaseHandler) {
        dialog(id)
    }

    private fun dialog(id: String) {
        // create web view
        val web = WebView(context)
        web.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )
        web.settings.javaScriptEnabled = true
        web.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(w: WebView, u: String) : Boolean {
                w.loadUrl(u)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.w("WebViewActivity", url.orEmpty())
            }
        })
        web.loadUrl("https://billing.getapp.store/v1/users/login?product=${id}")

        // create main view
        val height = (context.getResources().getDisplayMetrics().heightPixels / 1.5).toInt()
        val view = LinearLayout(context)
        view.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            height
        )

        view.setPadding(0, 32, 0, 0)
        view.addView(web)

        // create dialog
        val dialog = BottomSheetDialog(context) // if it is a activity than @DetailActivity
        dialog.setContentView(view)
        dialog.show()
    }

    fun consume(id: String) {
        // call api and consume purchase
    }

    fun restore() {
        // restore all purchase
        // load webview
        // track success results
    }

    companion object {
        lateinit var client: Billing

        fun init(token: String, api: String = "https://billing.getapp.store", context: Context) {
            client = Billing(token = token, api = api, context = context)
        }
    }
}