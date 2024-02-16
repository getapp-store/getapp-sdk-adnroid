package ru.kovardin.mediation.services

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.kovardin.mediation.settings.Settings
import java.io.IOException


interface ImpressionHandler {
    fun onFailure(e: Throwable)
    fun onSuccess()
}

data class ImpressionRequest(
    val unit: String,
    val revenue: Double,
    val data: String,
)

class ImpressionsService {
    private val tag = "ImpressionsService"
    private val client = OkHttpClient()

    fun impression(placement: Int, data: ImpressionRequest, callback: ImpressionHandler) {
        val url = "${Settings.base}/mediation/impressions/$placement/impression"

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

                callback.onSuccess()
            }
        } catch (e: Exception) {
            Log.e(tag, e.stackTraceToString())
            callback.onFailure(e)
        }
    }
}