package ru.kovardin.adapters.yandex

import android.app.Activity
import android.content.Context
import ru.kovardin.mediation.interfaces.BannerAdapter

class YandexAdsBannerAdapter: BannerAdapter {
    override fun load(context: Context) {
        TODO("Not yet implemented")
    }

    override fun show(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun bid(): Double {
        TODO("Not yet implemented")
    }

    override fun win(price: Double, bidder: String) {
        TODO("Not yet implemented")
    }

    override fun loss(price: Double, bidder: String, reason: Int) {
        TODO("Not yet implemented")
    }

    override fun network(): String {
        TODO("Not yet implemented")
    }
}