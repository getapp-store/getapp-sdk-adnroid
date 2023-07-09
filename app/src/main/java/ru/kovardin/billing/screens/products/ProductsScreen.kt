package ru.kovardin.billing.screens.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun ProductsScreen(model: ProductsViewModel, nav: NavHostController) {
    Column {
        model.products.forEach { product ->
            Row {
                Text(product.title)
                Text(product.amount.toString())
            }
            Divider()
        }
    }
}