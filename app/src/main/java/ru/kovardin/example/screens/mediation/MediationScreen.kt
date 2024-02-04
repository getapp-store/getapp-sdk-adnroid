package ru.kovardin.example.screens.mediation

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

@Composable
fun MediationScreen(model: MediationViewModel, nav: NavHostController) {
    val context = LocalContext.current as Activity
    Column {
        OutlinedButton(onClick = {
        //            interstitial.load(applicationContext)
            model.load(context)
        }) {
            Text(text = "load")
        }

        OutlinedButton(onClick = {
            model.show(context)
        }) {
            Text(text = "show")
        }
    }
}