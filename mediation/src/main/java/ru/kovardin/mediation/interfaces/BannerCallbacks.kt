package ru.kovardin.mediation.interfaces


interface BannerCallbacks {
    fun onLoad(ad: BannerAdapter)
    fun onNoAd(ad: BannerAdapter, reason: String)
    fun onImpression(ad: BannerAdapter, revenue: Double, data: String)
    fun onClick(ad: BannerAdapter)
    fun onFailure(ad: BannerAdapter?, message: String)
}

interface MediationBannerCallbacks : BannerCallbacks {
    fun onFinish()
}