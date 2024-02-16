package ru.kovardin.adapters.bigo

import android.content.Context
import android.util.Log
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerlCallbacks
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks
import sg.bigo.ads.BigoAdSdk
import sg.bigo.ads.api.AdConfig


class BigoAdapter : MediationAdapter {
    private val tag = "bigo"

    override fun init(context: Context, key: String) {
        Log.d(tag, "init bigo adapter")

        val config = AdConfig.Builder()
            .setAppId(key)
            .setDebug(true)
            .build()

        BigoAdSdk.initialize(context, config) {
            Log.i(tag, "initialized")
        }
    }

    override fun token(context: Context): String {
        return ""
    }

    override fun createInterstitial(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: InterstitialCallbacks
    ): InterstitialAdapter {
        return BigoInterstitialAdapter(
            context = context,
            placement = placement,
            unit = unit,
            callbacks = callbacks,
        )
    }

    override fun createBanner(
        context: Context,
        placement:
        Int,
        unit: String,
        callbacks: BannerlCallbacks,
    ): BannerAdapter {
        return BigoBannerAdapter(
            context = context,
            placement = placement,
            unit = unit,
            callbacks = callbacks,
        )
    }
}