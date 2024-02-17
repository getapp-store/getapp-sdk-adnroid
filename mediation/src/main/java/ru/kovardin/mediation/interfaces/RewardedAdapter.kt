package ru.kovardin.mediation.interfaces

import android.app.Activity

interface RewardedAdapter {
    fun load()
    fun show(activity: Activity)
    fun bid(): Double
    fun win(price: Double, bidder: String)
    fun loss(price: Double, bidder: String, reason: Int)
    fun network(): String
}