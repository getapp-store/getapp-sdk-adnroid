package ru.kovardin.adapters.bigo

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerCallbacks
import ru.kovardin.mediation.services.ImpressionHandler
import ru.kovardin.mediation.services.ImpressionRequest
import ru.kovardin.mediation.services.ImpressionsService
import sg.bigo.ads.api.AdBid
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdInteractionListener
import sg.bigo.ads.api.AdLoadListener
import sg.bigo.ads.api.AdSize
import sg.bigo.ads.api.BannerAd
import sg.bigo.ads.api.BannerAdLoader
import sg.bigo.ads.api.BannerAdRequest


class BigoBannerAdapter(
    private val context: Context, // TODO поправить контекст
    private val placement: Int,
    private val unit: String,
    private val callbacks: BannerCallbacks,
) : BannerAdapter {
    private val tag = "BigoBannerAdapter"

    var banner: BannerAd? = null
    private var bid: AdBid? = null

    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        val price = bid?.price ?: 0.0

        return price
    }

    override fun load() {
        // BannerAd.isExpired() // TODO нужно проверять протухание баннера
        val request = BannerAdRequest.Builder()
            .withSlotId(unit)
            .withAdSizes(AdSize.BANNER)
            .build()
        val loader = BannerAdLoader.Builder().withAdLoadListener(object : AdLoadListener<BannerAd> {
            override fun onError(error: AdError) {
                callbacks.onNoAd(this@BigoBannerAdapter, error.message)
            }

            override fun onAdLoaded(ad: BannerAd) {
                banner = ad

                banner?.setAdInteractionListener(object : AdInteractionListener {
                    override fun onAdError(error: AdError) {
                        callbacks.onFailure(this@BigoBannerAdapter, error.message)
                    }

                    override fun onAdImpression() {
                        val revenue = (bid?.price ?: 0.0) / 1000

                        scope.launch {
                            impressions.impression(
                                placement = placement,
                                data = ImpressionRequest(
                                    unit = unit,
                                    revenue = revenue, // цена одного показа
                                    data = ""
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
                            this@BigoBannerAdapter,
                            revenue = revenue,
                            data = "",
                        )
                    }

                    override fun onAdClicked() {
                        callbacks.onClick(this@BigoBannerAdapter)
                    }

                    override fun onAdOpened() {}
                    override fun onAdClosed() {}
                })

                bid = ad.bid // cpm

                callbacks.onLoad(this@BigoBannerAdapter)
            }
        }).build()
        loader.loadAd(request)
    }

    override fun view(context: Context): View {
        return banner?.adView() ?: TextView(context)
    }

    override fun win(price: Double, bidder: String) {
        bid?.notifyWin(price, bidder)
    }

    override fun loss(price: Double, bidder: String, reason: Int) {
        bid?.notifyLoss(price, bidder, reason)
    }

    override fun network(): String {
        return BigoAdapter.NETWORK
    }
}