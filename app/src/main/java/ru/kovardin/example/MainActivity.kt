package ru.kovardin.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ru.kovardin.adapters.bigo.BigoAdapter
import ru.kovardin.adapters.cpa.CpaAdapter
import ru.kovardin.adapters.mytraget.MyTargetAdapter
import ru.kovardin.adapters.yandex.YandexAdsAdapter
import ru.kovardin.example.ui.theme.BillingTheme
import ru.kovardin.boosty.Boosty
import ru.kovardin.getappbilling.Billing
import ru.kovardin.mediation.Interstitial
import ru.kovardin.mediation.Mediation
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks

class MainActivity : ComponentActivity() {
    val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Mediation.init(
            applicationContext,
            "1",
            adapters = mapOf(
                "yandex" to YandexAdsAdapter(),
                "mytarget" to MyTargetAdapter(),
                "cpa" to CpaAdapter(),
                "bigo" to BigoAdapter(),
            ),
        )

        val interstitial = Interstitial()

        // "1" - placement id
        interstitial.init("1", callbacks = object : InterstitialCallbacks {
            override fun onLoad(ad: InterstitialAdapter) {
                Log.d(tag, "on load")
            }

            override fun onImpression(ad: InterstitialAdapter, data: String) {
                Log.d(tag, data)
            }

            override fun onFailure(message: String) {
                Log.e(tag, message)
            }

        })

        Billing.init(
            context = this,
            app = "1",
            api = "https://service.getapp.store",
        )

        Boosty.init(
            context = this,
            app="1",
            api = "https://service.getapp.store",
        )

        super.onCreate(savedInstanceState)
        setContent {
            BillingTheme {
                BillingApp()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BillingTheme {
        BillingApp()
    }
}