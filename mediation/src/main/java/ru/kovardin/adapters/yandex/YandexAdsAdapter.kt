package ru.kovardin.adapters.yandex

import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.common.MobileAds
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks


class YandexAdsAdapter : MediationAdapter {
    private val tag = "yandex"

    override fun init(context: Context, key: String) {
        Log.d(tag, "init yandex adapter")

        MobileAds.initialize(context) {

        }
    }

    override fun token(context: Context): String {
        return ""
    }

    override fun createInterstitial(
        placement: Int,
        unit: String,
        callbacks: InterstitialCallbacks
    ): InterstitialAdapter {
        return YandexAdsInterstitialAdapter(placement = placement, unit = unit, callbacks = callbacks)
    }

    override fun createBanner(placement: Int, unit: String, callbacks: BannerAdapter): BannerAdapter {
        TODO("Not yet implemented")
    }
}