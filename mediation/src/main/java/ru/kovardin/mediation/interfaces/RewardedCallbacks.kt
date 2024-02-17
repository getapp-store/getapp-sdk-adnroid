package ru.kovardin.mediation.interfaces

interface RewardedCallbacks {
    fun onLoad(ad: RewardedAdapter)
    fun onNoAd(ad: RewardedAdapter, reason: String)
    fun onOpen(ad: RewardedAdapter)
    fun onImpression(ad: RewardedAdapter, revenue: Double, data: String)
    fun onClick(ad: RewardedAdapter)
    fun onClose(ad: RewardedAdapter)
    fun onReward(ad: RewardedAdapter, amount: Int, type: String)
    fun onFailure(ad: RewardedAdapter?, message: String)
}