package ru.kovardin.getappbilling


data class PurchaseResponse(
    val id: String,
    val status: String,
    val product: String,
    val name: String,
    val title: String,
    val amount: Int,
)

interface PurchaseHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: PurchaseResponse)
}