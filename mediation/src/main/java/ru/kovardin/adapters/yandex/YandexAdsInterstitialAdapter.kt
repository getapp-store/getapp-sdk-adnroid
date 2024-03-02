package ru.kovardin.adapters.yandex

import android.app.Activity
import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.adapters.yandex.utils.revenue
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks
import ru.kovardin.mediation.models.User
import ru.kovardin.mediation.services.AuctionService
import ru.kovardin.mediation.services.BidHandler
import ru.kovardin.mediation.services.BidRequest
import ru.kovardin.mediation.services.BidResponse
import ru.kovardin.mediation.services.ImpressionHandler
import ru.kovardin.mediation.services.ImpressionRequest
import ru.kovardin.mediation.services.ImpressionsService


class YandexAdsInterstitialAdapter(
    private val context: Context,
    private val placement: Int,
    private val unit: String,
    private val callbacks: InterstitialCallbacks,
) : InterstitialAdapter {
    private val tag = "YandexAdsInterstitialAdapter"

    private var cpm: Double = 0.0
    private var bid: Double = 0.0

    private var interstitial: InterstitialAd? = null
    private var loader: InterstitialAdLoader? = null

    private val auction = AuctionService()
    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        return bid
    }

    override fun load() {
        loader = InterstitialAdLoader(context).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
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

                    ad.setAdEventListener(object : InterstitialAdEventListener {
                        override fun onAdShown() {
                            callbacks.onOpen(this@YandexAdsInterstitialAdapter)
                        }

                        override fun onAdFailedToShow(adError: AdError) {
                            interstitial?.setAdEventListener(null)
                            interstitial = null

                            callbacks.onFailure(this@YandexAdsInterstitialAdapter, adError.description)

                            this@YandexAdsInterstitialAdapter.load()
                        }

                        override fun onAdDismissed() {
                            interstitial?.setAdEventListener(null)
                            interstitial = null

                            callbacks.onClose(this@YandexAdsInterstitialAdapter)

                            this@YandexAdsInterstitialAdapter.load()
                        }

                        override fun onAdClicked() {
                            callbacks.onClick(this@YandexAdsInterstitialAdapter)
                        }

                        override fun onAdImpression(data: ImpressionData?) {
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
                                this@YandexAdsInterstitialAdapter,
                                revenue = data?.revenue() ?: 0.0,
                                data = data?.rawData.orEmpty(),
                            )
                        }
                    })

                    interstitial = ad
                    callbacks.onLoad(this@YandexAdsInterstitialAdapter)
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                    Log.d(tag, adRequestError.description)

                    callbacks.onNoAd(this@YandexAdsInterstitialAdapter, adRequestError.description)
                }
            })
        }

        val cfg = AdRequestConfiguration.Builder(unit).build()
        loader?.loadAd(cfg)
    }

    override fun show(activity: Activity) {
        interstitial?.show(activity)
    }

    override fun win(price: Double, bidder: String) {
        Log.i(tag, "win price: $price, bidder: $bidder")
    }

    override fun loss(price: Double, bidder: String, reason: Int) {
        Log.i(tag, "loss price: $price, bidder: $bidder, reason: $reason")
    }

    override fun network(): String {
        return YandexAdsAdapter.NETWORK
    }
}