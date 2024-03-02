package ru.kovardin.mediation.interfaces


interface InterstitialCallbacks {
    fun onLoad(ad: InterstitialAdapter)
    fun onNoAd(ad: InterstitialAdapter, reason: String)
    fun onOpen(ad: InterstitialAdapter)
    fun onImpression(ad: InterstitialAdapter, revenue: Double, data: String)
    fun onClick(ad: InterstitialAdapter)
    fun onClose(ad: InterstitialAdapter)
    fun onFailure(ad: InterstitialAdapter?, message: String)
}

interface MediationInterstitialCallbacks : InterstitialCallbacks {
    fun onFinish()
}