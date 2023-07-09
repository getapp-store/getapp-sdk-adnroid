package ru.kovardin.billing.screens.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import ru.kovardin.billing.Billing
import ru.kovardin.billing.Product
import ru.kovardin.billing.ProductsHandler
import ru.kovardin.billing.ProductsResponse
import ru.kovardin.billing.PurchaseHandler
import ru.kovardin.billing.PurchaseResponse

class HomeViewModel : ViewModel() {
    val products = mutableStateListOf<Product>()

    fun fetch() {
        products.clear()

        Billing.client.products(object : ProductsHandler {
            override fun onFailure(e: Throwable) {
                //
            }

            override fun onSuccess(resp: ProductsResponse) {
                products.addAll(resp.items)
            }
        })
    }

    fun purchase(id: String) {
        Billing.client.purchase(id, object : PurchaseHandler {
            override fun onFailure(e: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(resp: PurchaseResponse) {
                TODO("Not yet implemented")
            }
        })
    }
}