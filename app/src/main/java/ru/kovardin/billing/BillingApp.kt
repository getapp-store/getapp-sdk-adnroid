package ru.kovardin.billing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import ru.kovardin.billing.screens.boosty.BoostyScreen
import ru.kovardin.billing.screens.boosty.BoostyViewModel
import ru.kovardin.billing.screens.home.HomeScreen
import ru.kovardin.billing.screens.home.HomeViewModel
import ru.kovardin.billing.screens.payments.PaymentsScreen
import ru.kovardin.billing.screens.payments.PaymentsViewModel
import ru.kovardin.billing.screens.products.ProductsScreen
import ru.kovardin.billing.screens.products.ProductsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingApp(modifier: Modifier = Modifier) {
    val nav = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background
        ) {
            val homeViewModel = HomeViewModel()
            val productsViewModel = ProductsViewModel()
            val paymentsViewModel = PaymentsViewModel()
            val boostyViewModel = BoostyViewModel()

            NavHost(navController = nav, startDestination = "home") {
                composable("home") {
                    HomeScreen(model = homeViewModel, nav = nav)
                }
                composable("products") {
                    ProductsScreen(model = productsViewModel, nav = nav)
                }
                composable("payments") {
                    PaymentsScreen(model = paymentsViewModel, nav = nav)
                }
                composable("boosty") {
                    BoostyScreen(model = boostyViewModel, nav = nav)
                }
            }
        }
    }
}