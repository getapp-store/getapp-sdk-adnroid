package ru.kovardin.adapters.bigo

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerCallbacks
import ru.kovardin.mediation.interfaces.InitializedCallbacks
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.interfaces.RewardedAdapter
import ru.kovardin.mediation.interfaces.RewardedCallbacks
import sg.bigo.ads.BigoAdSdk
import sg.bigo.ads.api.AdConfig


class BigoAdapter : MediationAdapter {

    override fun init(context: Context, key: String, callbacks: InitializedCallbacks) {
        Log.d(NETWORK, "init bigo adapter")

        val config = AdConfig.Builder()
            .setAppId(key)
            .setDebug(true)
            .build()

        BigoAdSdk.initialize(context, config) {
            Handler(Looper.getMainLooper()).post(Runnable {
                callbacks.onInitialized(NETWORK)
            })

            Log.i(NETWORK, "initialized")
        }
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
        callbacks: BannerCallbacks,
    ): BannerAdapter {
        return BigoBannerAdapter(
            context = context,
            placement = placement,
            unit = unit,
            callbacks = callbacks,
        )
    }

    override fun createRewarded(
        context: Context,
        placement: Int,
        unit: String,
        callbacks:
        RewardedCallbacks,
    ): RewardedAdapter {
        return BigoRewardedAdapter(
            context = context,
            placement = placement,
            unit = unit,
            callbacks = callbacks,
        )
    }

    companion object {
        const val NETWORK = "yandex"
    }
}