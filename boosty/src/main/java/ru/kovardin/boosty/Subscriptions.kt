package ru.kovardin.boosty

data class Subscription(
    val id: String,
    val external: String,
    val name: String,
    val title: String,
    val amount: Int,
    val blog: String
)

data class SubscriptionsResponse(val items: List<Subscription>)

interface SubscriptionsHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: SubscriptionsResponse)
}