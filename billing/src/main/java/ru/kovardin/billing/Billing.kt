package ru.kovardin.billing

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
        // load webview
        // track success results
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

        fun init(token: String, api: String = "https://billing.getapp.store") {
            client = Billing(token = token, api = api)
        }
    }
}