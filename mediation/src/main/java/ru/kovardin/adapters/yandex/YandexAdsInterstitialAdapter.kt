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
    private val placement: Int,
    private val unit: String,
    private val callbacks: InterstitialCallbacks,
) : InterstitialAdapter {
    private val tag = "YandexAdsInterstitialAdapter"
    private val network = "yandex"

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

    override fun load(context: Context) {
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
                            // Called when ad is shown.
                        }

                        override fun onAdFailedToShow(adError: AdError) {
                            // Called when an InterstitialAd failed to show.
                            // Clean resources after Ad dismissed
                            interstitial?.setAdEventListener(null)
                            interstitial = null

                            // Now you can preload the next interstitial ad.
                            this@YandexAdsInterstitialAdapter.load(context)
                        }

                        override fun onAdDismissed() {
                            // Called when ad is dismissed.
                            // Clean resources after Ad dismissed
                            interstitial?.setAdEventListener(null)
                            interstitial = null

                            // Now you can preload the next interstitial ad.
                            this@YandexAdsInterstitialAdapter.load(context)
                        }

                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                        }

                        override fun onAdImpression(data: ImpressionData?) {
                            scope.launch {
                                impressions.impression(
                                    placement = placement,
                                    data = ImpressionRequest(
                                        unit = unit,
                                        data = data?.rawData.orEmpty(),
                                        revenue = cpm / 1000
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

                            callbacks.onImpression(this@YandexAdsInterstitialAdapter, data?.rawData.orEmpty())
                        }
                    })

                    interstitial = ad
                    callbacks.onLoad(this@YandexAdsInterstitialAdapter)
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                    Log.d(tag, adRequestError.description)

                    callbacks.onFailure(adRequestError.description)
                }
            })
        }

        loadInterstitial()
    }

    private fun loadInterstitial() {
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
        return network
    }
}