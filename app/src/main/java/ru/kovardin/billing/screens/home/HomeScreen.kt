package ru.kovardin.billing.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(model: HomeViewModel, nav: NavHostController) {
    Column {
        model.products.forEach { product ->
            Row {
                Text(product.title)
                Text(product.amount.toString())
                Button(onClick = {
                    model.purchase(product.id)
                }) {
                    Text(text = "Buy")
                }
            }
            Divider()
        }
    }
}