package ru.kovardin.mediation.services

import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.kovardin.mediation.models.Unit
import ru.kovardin.mediation.settings.Settings
import java.io.IOException


interface PlacementsHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: PlacementResponse)
}

data class PlacementResponse(
    val placement: String,
    val format: String,
    val units: List<Unit>,
)

class PlacementsService {

    private val tag = "PlacementsService"
    private val client = OkHttpClient()

    fun get(placement: String, callback: PlacementsHandler) {
        val url = "${Settings.base}/mediation/placements/$placement"

        Log.d(tag, "request $url")

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    callback.onFailure(IOException("Unexpected code $response"))
                    return
                }

                val body = response.body?.string().orEmpty()
                val resp: PlacementResponse

                try {
                    resp = Gson().fromJson(body, PlacementResponse::class.java)
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