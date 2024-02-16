package ru.kovardin.adapters.mytraget

import android.content.Context
import android.view.View
import ru.kovardin.mediation.interfaces.BannerAdapter

class MyTargetBannerAdapter: BannerAdapter {
    override fun load() {
        TODO("Not yet implemented")
    }

    override fun view(context: Context): View {
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