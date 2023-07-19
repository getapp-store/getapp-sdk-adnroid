package ru.kovardin.billing.screens.products

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import ru.kovardin.getappbilling.Billing
import ru.kovardin.getappbilling.Product
import ru.kovardin.getappbilling.ProductsHandler
import ru.kovardin.getappbilling.ProductsResponse

class ProductsViewModel: ViewModel() {
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
}
