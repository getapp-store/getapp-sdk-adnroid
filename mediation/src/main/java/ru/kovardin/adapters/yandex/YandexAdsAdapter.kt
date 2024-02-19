package ru.kovardin.adapters.yandex

import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.common.MobileAds
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerCallbacks
import ru.kovardin.mediation.interfaces.MediationAdapter
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks
import ru.kovardin.mediation.interfaces.RewardedAdapter
import ru.kovardin.mediation.interfaces.RewardedCallbacks


class YandexAdsAdapter : MediationAdapter {
    private val tag = "yandex"

    override fun init(context: Context, key: String) {
        Log.d(tag, "init yandex adapter")

        MobileAds.initialize(context) {

        }
    }

    override fun createInterstitial(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: InterstitialCallbacks
    ): InterstitialAdapter {
        return YandexAdsInterstitialAdapter(
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
        return YandexAdsBannerAdapter(
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
        return YandexAdsRewardedAdapter(
            context = context,
            placement = placement,
            unit = unit,
            callbacks = callbacks,
        )
    }
}