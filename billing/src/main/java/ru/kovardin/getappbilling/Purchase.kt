package ru.kovardin.getappbilling


data class PurchaseResponse(val id: String, val status: String, val product: String)

interface PurchaseHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: PurchaseResponse)
}