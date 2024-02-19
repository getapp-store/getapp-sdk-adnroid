package ru.kovardin.adapters.cpa

import android.content.Context
import android.util.Log
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerCallbacks
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks
import ru.kovardin.mediation.interfaces.RewardedAdapter
import ru.kovardin.mediation.interfaces.RewardedCallbacks


class CpaAdapter : MediationAdapter {
    private val tag = "cpa"

    override fun init(context: Context, key: String) {
        Log.d(tag, "init cpa adapter")
    }

    override fun createInterstitial(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: InterstitialCallbacks
    ): InterstitialAdapter {
        return CpaInterstitialAdapter(
            context = context,
            placement = placement,
            unit = unit,
            callbacks = callbacks,
        )
    }

    override fun createBanner(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: BannerCallbacks,
    ): BannerAdapter {
        TODO("Not yet implemented")
    }

    override fun createRewarded(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: RewardedCallbacks,
    ): RewardedAdapter {
        TODO("Not yet implemented")
    }
}