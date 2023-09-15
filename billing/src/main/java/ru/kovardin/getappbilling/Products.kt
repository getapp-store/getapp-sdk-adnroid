package ru.kovardin.getappbilling


data class Product(val id: String, val name: String, val title: String, val amount: Int)

data class ProductsResponse(val items: List<Product>)

interface ProductsHandler {
    fun onFailure(e: Throwable)
    fun onSuccess(resp: ProductsResponse)
}
