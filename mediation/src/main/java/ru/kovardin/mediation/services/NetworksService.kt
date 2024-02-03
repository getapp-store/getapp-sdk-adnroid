package ru.kovardin.mediation.services

import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.kovardin.mediation.models.Network
import ru.kovardin.mediation.settings.Settings
import java.io.IOException


interface NetworkHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: NetworkResponse)
}

data class NetworkResponse(
    val key: String,
    val networks: List<Network>,
)

class NetworksService {

    private val tag = "NetworksService"
    private val client = OkHttpClient()

    // load all networks from server
    fun fetch(id: String, callback: NetworkHandler) {
        val url = "${Settings.base}/mediation/networks/$id"

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
                val resp: NetworkResponse

                try {
                    resp = Gson().fromJson(body, NetworkResponse::class.java)
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