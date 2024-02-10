package ru.kovardin.adapters.cpa

import android.content.Context
import android.util.Log
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks


class CpaAdapter: MediationAdapter {
    private val tag = "cpa"

    override fun init(context: Context, key: String) {
        Log.d(tag, "init cpa adapter")
    }

    override fun token(context: Context): String {
        return ""
    }

    override fun createInterstitial(
        placement: Int,
        unit: String,
        callbacks: InterstitialCallbacks
    ): InterstitialAdapter {
        return CpaInterstitialAdapter(placement = placement, unit = unit, callbacks = callbacks)
    }

    override fun createBanner(placement: Int, unit: String, callbacks: BannerAdapter): BannerAdapter {
        TODO("Not yet implemented")
    }
}