package ru.kovardin.example.screens.boosty

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import ru.kovardin.boosty.Boosty
import ru.kovardin.boosty.SubscriptionsHandler
import ru.kovardin.boosty.SubscriptionsResponse


@Composable
fun BoostyScreen(model: BoostyViewModel, nav: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(Unit){
        model.fetch()
    }

    Column {
        Text("Boosty")

        if (model.subscribed.value) {
            Text("Спасибо за подписку!")
        } else {
            model.subscriptions.forEach { subscription ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(subscription.title + ": ")
                    Text(subscription.amount.toString())
                    Button(onClick = {
                        model.subscribe(context, subscription.external)
                    }) {
                        Text(text = "Subscribe")
                    }
                }
                Divider()
            }

            Button(onClick = {
                model.subscribe(context)
            }) {
                Text(text = "Subscribe")
            }
        }
    }
}