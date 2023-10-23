package ru.kovardin.boosty

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import java.net.HttpCookie
import java.net.URLDecoder

class BoostyUser(val name: String, val avatarUrl: String, val provider: String) {
    fun external(): String {
        val u = Uri.parse(avatarUrl)
        if (u.pathSegments.count() > 1) {
            return u.pathSegments[1]
        }

        return ""
    }

    companion object {
        fun parse(cookie: String): BoostyUser?  {
            for (cc in cookie.split(";")) {
                val cookies = HttpCookie.parse(cc)
                for (c in cookies) {
                    if (c.name == "last_acc") {
                        println(URLDecoder.decode(c.value, "UTF-8"))

                        val auth = URLDecoder.decode(c.value, "UTF-8")
                        var resp: BoostyUser

                        try {
                            resp = Gson().fromJson(auth, BoostyUser::class.java)
                        } catch (e: Exception) {
                            Log.e("Billing", e.message.orEmpty())
                            return null
                        }

                        return resp
                    }
                }
            }

            return null
        }
    }
}
