package ru.kovardin.adapters.mytraget

import android.content.Context
import android.util.Log
import com.my.target.common.MyTargetManager
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerlCallbacks
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks
import ru.kovardin.mediation.interfaces.RewardedAdapter
import ru.kovardin.mediation.interfaces.RewardedCallbacks


class MyTargetAdapter : MediationAdapter {
    private val tag = "mytarget"

    override fun init(context: Context, key: String) {
        Log.d(tag, "init mytarget adapter")
        MyTargetManager.setDebugMode(true)
        MyTargetManager.initSdk(context)
    }

    override fun createInterstitial(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: InterstitialCallbacks
    ): InterstitialAdapter {
        return MyTargetInterstitialAdapter(
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
        callbacks: BannerlCallbacks,
    ): BannerAdapter {
        return MyTargetBannerAdapter(
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
        callbacks: RewardedCallbacks,
    ): RewardedAdapter {
        return MyTargetRewardedAdapter(
            context = context,
            placement = placement,
            unit = unit,
            callbacks = callbacks,
        )
    }
}