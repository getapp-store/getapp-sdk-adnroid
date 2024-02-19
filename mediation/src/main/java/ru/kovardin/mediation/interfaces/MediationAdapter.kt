package ru.kovardin.mediation.interfaces

import android.content.Context


interface MediationAdapter {
    fun init(context: Context, key: String)

    fun createInterstitial(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: InterstitialCallbacks,
    ): InterstitialAdapter

    fun createBanner(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: BannerCallbacks,
    ): BannerAdapter

    fun createRewarded(
        context: Context,
        placement: Int,
        unit: String,
        callbacks: RewardedCallbacks,
    ): RewardedAdapter
}