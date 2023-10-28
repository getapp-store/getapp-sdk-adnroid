package ru.kovardin.boosty

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import java.net.HttpCookie
import java.net.URLDecoder

const val UserCookieKey = "last_acc"

class User(val name: String, val avatarUrl: String, val provider: String) {
    fun external(): String {
        val u = Uri.parse(avatarUrl)
        if (u.pathSegments.count() > 1) {
            return u.pathSegments[1]
        }

        return ""
    }

    companion object {
        fun parse(cookie: String): User?  {
            for (cc in cookie.split(";")) {
                val cookies = HttpCookie.parse(cc)
                for (c in cookies) {
                    if (c.name == UserCookieKey) {
                        val auth = URLDecoder.decode(c.value, "UTF-8")
                        var resp: User

                        try {
                            resp = Gson().fromJson(auth, User::class.java)
                        } catch (e: Exception) {
                            Log.e("User", e.message.orEmpty())
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
