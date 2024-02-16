package ru.kovardin.adapters.yandex.utils

import com.google.gson.Gson
import com.yandex.mobile.ads.common.ImpressionData

data class YandexData(
    val revenueUSD: Double
)

fun ImpressionData.revenue(): Double {
    var resp: YandexData
    try {
        resp = Gson().fromJson(this.rawData, YandexData::class.java)
    } catch (e: Exception) {
        return 0.0
    }

    return resp.revenueUSD
}