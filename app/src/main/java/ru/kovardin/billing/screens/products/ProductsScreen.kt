package ru.kovardin.billing.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun ProductsScreen(model: ProductsViewModel, nav: NavHostController) {
    LaunchedEffect(Unit){
        model.fetch()
    }

    Column {
        model.products.forEach { product ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(product.title + ": ")
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