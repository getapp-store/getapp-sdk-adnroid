package ru.kovardin.mediation.services

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.kovardin.mediation.models.User
import ru.kovardin.mediation.settings.Settings
import java.io.IOException


interface BidHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: BidResponse)
}

data class BidRequest(
    val unit: String,
    val user: User,
)

data class BidResponse(
    val unit: String,
    val bid: Double,
    val cpm: Double,
)

class AuctionService {
    private val tag = "AuctionService"
    private val client = OkHttpClient()

    fun bid(placement: Int, data: BidRequest, callback: BidHandler) {
        val url = "${Settings.base}/mediation/auction/$placement/bid"

        Log.d(tag, "request $url")

        val json = Gson().toJson(data)
        val req = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .post(req)
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    callback.onFailure(IOException("Unexpected code $response"))
                    return
                }

                val body = response.body?.string().orEmpty()
                val resp: BidResponse

                try {
                    resp = Gson().fromJson(body, BidResponse::class.java)
                } catch (e: Exception) {
                    Log.d(tag, e.message.orEmpty())
                    callback.onFailure(e)
                    return
                }

                callback.onSuccess(resp)
            }
        } catch (e: Exception) {
            Log.e(tag, e.stackTraceToString())
            callback.onFailure(e)
        }
    }
}