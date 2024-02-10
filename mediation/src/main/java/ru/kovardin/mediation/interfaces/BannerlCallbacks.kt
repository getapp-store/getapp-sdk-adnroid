package ru.kovardin.mediation.interfaces


interface BannerlCallbacks {
    fun onLoad(ad: BannerAdapter)
    fun onNoAd(ad: BannerAdapter, reason: String)
    fun onImpression(ad: BannerAdapter, data: String)
    fun onClick(ad: BannerAdapter)
    fun onFailure(ad: BannerAdapter?, message: String)
}