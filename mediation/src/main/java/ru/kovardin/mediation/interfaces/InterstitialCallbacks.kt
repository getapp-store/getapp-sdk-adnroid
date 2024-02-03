package ru.kovardin.mediation.interfaces


interface InterstitialCallbacks {
    fun onLoad(ad: InterstitialAdapter)
    fun onImpression(ad: InterstitialAdapter, data: String)
    fun onFailure(message: String)
}