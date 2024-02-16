package ru.kovardin.example.screens.mediation

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun MediationScreen(model: MediationViewModel, nav: NavHostController) {
    val context = LocalContext.current as Activity
    var loaded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(onClick = {
            model.loadInterstitial(context)
            model.loadBanner(context)
        }) {
            Text(text = "reload")
        }

        OutlinedButton(onClick = {
            model.showInterstitial(context)
        }) {
            Text(text = "interstitial")
        }

        OutlinedButton(onClick = {
            loaded = true
        }) {
            Text(text = "banner")
        }

        if (loaded) {
            BannerView(model)
        }
    }
}

@Composable
fun BannerView(model: MediationViewModel) {
    AndroidView(
        modifier = Modifier
            .height(100.dp)
            .width(300.dp), // Occupy the max size in the Compose UI tree
        factory = { context ->
            model.showBanner(context) ?: TextView(context)
        },
        update = { view ->

        }
    )
}
