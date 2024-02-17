package ru.kovardin.adapters.yandex

import android.app.Activity
import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.adapters.yandex.utils.revenue
import ru.kovardin.mediation.interfaces.RewardedAdapter
import ru.kovardin.mediation.interfaces.RewardedCallbacks
import ru.kovardin.mediation.models.User
import ru.kovardin.mediation.services.AuctionService
import ru.kovardin.mediation.services.BidHandler
import ru.kovardin.mediation.services.BidRequest
import ru.kovardin.mediation.services.BidResponse
import ru.kovardin.mediation.services.ImpressionHandler
import ru.kovardin.mediation.services.ImpressionRequest
import ru.kovardin.mediation.services.ImpressionsService

class YandexAdsRewardedAdapter(
    private val context: Context,
    private val placement: Int,
    private val unit: String,
    private val callbacks: RewardedCallbacks,
): RewardedAdapter {
    private val tag = "YandexAdsRewardedAdapter"
    private val network = "yandex"

    private var cpm: Double = 0.0
    private var bid: Double = 0.0

    private var rewarded: RewardedAd? = null
    private var loader: RewardedAdLoader? = null

    private val auction = AuctionService()
    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        return bid
    }

    override fun load() {
        loader = RewardedAdLoader(context).apply {
            setAdLoadListener(object : RewardedAdLoadListener {
                override fun onAdLoaded(ad: RewardedAd) {
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

                    ad.setAdEventListener(object : RewardedAdEventListener {
                        override fun onAdShown() {
                            callbacks.onOpen(this@YandexAdsRewardedAdapter)
                        }

                        override fun onAdFailedToShow(adError: AdError) {
                            rewarded?.setAdEventListener(null)
                            rewarded = null

                            callbacks.onFailure(this@YandexAdsRewardedAdapter, adError.description)

                            this@YandexAdsRewardedAdapter.load()
                        }

                        override fun onAdDismissed() {
                            rewarded?.setAdEventListener(null)
                            rewarded = null

                            callbacks.onClose(this@YandexAdsRewardedAdapter)

                            this@YandexAdsRewardedAdapter.load()
                        }

                        override fun onAdClicked() {
                            callbacks.onClick(this@YandexAdsRewardedAdapter)
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
                                this@YandexAdsRewardedAdapter,
                                revenue = data?.revenue() ?: 0.0,
                                data = data?.rawData.orEmpty(),
                            )
                        }

                        override fun onRewarded(r: Reward) {
                            callbacks.onReward(this@YandexAdsRewardedAdapter, r.amount,  r.type)
                        }
                    })

                    rewarded = ad
                    callbacks.onLoad(this@YandexAdsRewardedAdapter)
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                    Log.d(tag, adRequestError.description)

                    callbacks.onNoAd(this@YandexAdsRewardedAdapter, adRequestError.description)
                }
            })
        }

        val cfg = AdRequestConfiguration.Builder(unit).build()
        loader?.loadAd(cfg)
    }

    override fun show(activity: Activity) {
        rewarded?.show(activity)
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