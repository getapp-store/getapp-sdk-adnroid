package ru.kovardin.adapters.mytraget

import android.content.Context
import android.util.Log
import com.my.target.common.MyTargetManager
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks


class MyTargetAdapter : MediationAdapter {
    private val tag = "mytarget"

    override fun init(context: Context, key: String) {
        Log.d(tag, "init mytarget adapter")
        MyTargetManager.setDebugMode(true)
        MyTargetManager.initSdk(context)
    }

    override fun token(context: Context): String {
        return MyTargetManager.getBidderToken(context)
    }

    override fun createInterstitial(
        placement: Int,
        unit: String,
        callbacks: InterstitialCallbacks
    ): InterstitialAdapter {
        return MyTargetInterstitialAdapter(placement = placement, unit = unit, callbacks = callbacks)
    }

    override fun createBanner(placement: Int, unit: String, callbacks: BannerAdapter): BannerAdapter {
        TODO("Not yet implemented")
    }
}