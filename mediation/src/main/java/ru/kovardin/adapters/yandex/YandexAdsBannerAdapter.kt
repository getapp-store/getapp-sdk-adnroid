package ru.kovardin.adapters.yandex

import android.content.Context
import android.util.Log
import android.view.View
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.adapters.yandex.utils.revenue
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerCallbacks
import ru.kovardin.mediation.models.User
import ru.kovardin.mediation.services.AuctionService
import ru.kovardin.mediation.services.BidHandler
import ru.kovardin.mediation.services.BidRequest
import ru.kovardin.mediation.services.BidResponse
import ru.kovardin.mediation.services.ImpressionHandler
import ru.kovardin.mediation.services.ImpressionRequest
import ru.kovardin.mediation.services.ImpressionsService

class YandexAdsBannerAdapter(
    private val context: Context,
    private val placement: Int,
    private val unit: String,
    private val callbacks: BannerCallbacks,
) : BannerAdapter {
    private val tag = "YandexAdsInterstitialAdapter"
    private val network = "yandex"

    private val banner = BannerAdView(context)

    private var cpm: Double = 0.0
    private var bid: Double = 0.0

    private val auction = AuctionService()
    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun bid(): Double {
        return bid
    }

    override fun load() {
        banner.setAdSize(BannerAdSize.inlineSize(context, 300, 100))
        banner.setAdUnitId(unit)
        banner.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                scope.launch {
                    auction.bid(placement, BidRequest(
                        unit = unit,
                        user = User(id = "1")
                    ), object : BidHandler {
                        override fun onFailure(e: Throwable) {
                            Log.e(tag, e.message.toString())
                        }

                        override fun onSuccess(resp: BidResponse) {
                            cpm = resp.cpm
                            bid = resp.bid
                        }
                    })
                }

                callbacks.onLoad(this@YandexAdsBannerAdapter)
            }

            override fun onAdFailedToLoad(err: AdRequestError) {
                callbacks.onNoAd(this@YandexAdsBannerAdapter, err.description)
            }

            override fun onAdClicked() {
                callbacks.onClick(this@YandexAdsBannerAdapter)
            }

            override fun onLeftApplication() {}

            override fun onReturnedToApplication() {}

            override fun onImpression(data: ImpressionData?) {
                val revenue = cpm / 1000

                scope.launch {
                    impressions.impression(
                        placement = placement,
                        data = ImpressionRequest(
                            unit = unit,
                            revenue = data?.revenue() ?: revenue,
                            data = data?.rawData ?: "",
                        ),
                        callback = object : ImpressionHandler {
                            override fun onFailure(e: Throwable) {
                                Log.e(tag, e.message.toString())
                            }

                            override fun onSuccess() {
                                Log.i(tag, "write impression")
                            }
                        }
                    )
                }

                callbacks.onImpression(
                    this@YandexAdsBannerAdapter,
                    revenue = data?.revenue() ?: 0.0,
                    data = data?.rawData ?: "",
                )
            }
        })

        banner.loadAd(AdRequest.Builder().build())
    }

    override fun view(context: Context): View {
        return banner
    }

    override fun win(price: Double, bidder: String) {
        Log.i(tag, "win price: $price, bidder: $bidder")
    }

    override fun loss(price: Double, bidder: String, reason: Int) {
        Log.i(tag, "loss price: $price, bidder: $bidder, reason: $reason")
    }

    override fun network(): String {
        return network
    }
}