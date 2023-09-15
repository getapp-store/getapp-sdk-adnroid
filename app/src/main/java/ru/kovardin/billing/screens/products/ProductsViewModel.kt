package ru.kovardin.billing.screens.products

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import ru.kovardin.getappbilling.Billing
import ru.kovardin.getappbilling.Product
import ru.kovardin.getappbilling.ProductsHandler
import ru.kovardin.getappbilling.ProductsResponse
import ru.kovardin.getappbilling.PurchaseHandler
import ru.kovardin.getappbilling.PurchaseResponse

class ProductsViewModel: ViewModel() {
    val products = mutableStateListOf<Product>()

    fun fetch() {
        products.clear()

        Billing.client.products(object : ProductsHandler {
            override fun onFailure(e: Throwable) {
                Log.e("HomeViewModel", e.message.toString())
            }

            override fun onSuccess(resp: ProductsResponse) {
                products.addAll(resp.items)
            }
        })
    }

    fun purchase(id: String) {
        Billing.client.purchase(id, object : PurchaseHandler {
            override fun onFailure(e: Throwable) {
                Log.e("HomeViewModel", e.message.toString())
            }

            override fun onSuccess(resp: PurchaseResponse) {
                Log.i("HomeViewModel", "success. payment=${resp.id}, status=${resp.status}, product=${resp.product}")
            }
        })
    }
}
