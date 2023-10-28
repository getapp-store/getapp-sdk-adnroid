package ru.kovardin.boosty

data class Blog(
    val id: Int,
    val name: String,
    val title: String,
    val url: String
)

interface BlogHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: Blog)
}