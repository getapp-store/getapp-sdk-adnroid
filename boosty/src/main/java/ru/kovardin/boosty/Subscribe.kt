package ru.kovardin.boosty;


data class Subscriber(
    val id: String,
    val external: String,
    val name: String,
    val active: Boolean,
    val amount: Int,
    val subscription: Subscription
)

interface SubscribeHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: Subscriber)
}