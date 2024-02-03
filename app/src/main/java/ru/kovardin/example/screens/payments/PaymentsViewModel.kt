package ru.kovardin.example.screens.payments

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import ru.kovardin.getappbilling.Billing
import ru.kovardin.getappbilling.Payment
import ru.kovardin.getappbilling.Product
import ru.kovardin.getappbilling.ProductsHandler
import ru.kovardin.getappbilling.ProductsResponse
import ru.kovardin.getappbilling.PurchaseHandler
import ru.kovardin.getappbilling.PurchaseResponse
import ru.kovardin.getappbilling.RestoreHandler
import ru.kovardin.getappbilling.RestoreResponse

class PaymentsViewModel : ViewModel() {
    val payments = mutableStateListOf<Payment>()

    fun fetch() {
        payments.clear()

        Billing.client.restore(object : RestoreHandler {
            override fun onFailure(e: Throwable) {
                Log.e("HomeViewModel", e.message.toString())
            }

            override fun onSuccess(resp: RestoreResponse) {
                payments.addAll(resp.items)
            }
        })
    }
}