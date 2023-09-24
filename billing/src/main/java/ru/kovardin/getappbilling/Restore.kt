package ru.kovardin.getappbilling


data class Payment(
    val id: String,
    val status: String,
    val product: String,
    val name: String,
    val title: String,
    val amount: Int,
)

data class RestoreResponse(val items: List<Payment>)

interface RestoreHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: RestoreResponse)
}
