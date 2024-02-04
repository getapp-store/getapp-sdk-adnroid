package ru.kovardin.example

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
import ru.kovardin.example.screens.boosty.BoostyScreen
import ru.kovardin.example.screens.boosty.BoostyViewModel
import ru.kovardin.example.screens.home.HomeScreen
import ru.kovardin.example.screens.home.HomeViewModel
import ru.kovardin.example.screens.mediation.MediationScreen
import ru.kovardin.example.screens.mediation.MediationViewModel
import ru.kovardin.example.screens.payments.PaymentsScreen
import ru.kovardin.example.screens.payments.PaymentsViewModel
import ru.kovardin.example.screens.products.ProductsScreen
import ru.kovardin.example.screens.products.ProductsViewModel


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
            val mediationViewModel = MediationViewModel()

            mediationViewModel.init()

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
                composable("mediation") {
                    MediationScreen(model = mediationViewModel, nav = nav)
                }
            }
        }
    }
}