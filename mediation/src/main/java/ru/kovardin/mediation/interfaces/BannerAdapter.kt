package ru.kovardin.mediation.interfaces

import android.content.Context
import android.view.View

interface BannerAdapter {
    fun load()
    fun view(context: Context): View
    fun bid(): Double
    fun win(price: Double, bidder: String)
    fun loss(price: Double, bidder: String, reason: Int)
    fun network(): String
}