package ru.kovardin.getappbilling


data class AuthResponse(val token: String)

internal interface AuthHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: AuthResponse)
}