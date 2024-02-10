package ru.kovardin.mediation.interfaces

import android.content.Context


interface MediationAdapter {
    fun init(context: Context, key: String)
    fun token(context: Context): String
    fun createInterstitial(placement: Int, unit: String, callbacks: InterstitialCallbacks): InterstitialAdapter
    fun createBanner(placement: Int, unit: String, callbacks: BannerAdapter): BannerAdapter
}