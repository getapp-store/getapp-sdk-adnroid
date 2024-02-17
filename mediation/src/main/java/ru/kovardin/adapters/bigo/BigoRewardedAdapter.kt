package ru.kovardin.adapters.bigo

import android.app.Activity
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.mediation.interfaces.RewardedAdapter
import ru.kovardin.mediation.interfaces.RewardedCallbacks
import ru.kovardin.mediation.services.ImpressionHandler
import ru.kovardin.mediation.services.ImpressionRequest
import ru.kovardin.mediation.services.ImpressionsService
import ru.kovardin.mediation.settings.Settings
import sg.bigo.ads.api.AdBid
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdLoadListener
import sg.bigo.ads.api.RewardVideoAd
import sg.bigo.ads.api.RewardAdInteractionListener
import sg.bigo.ads.api.RewardVideoAdLoader
import sg.bigo.ads.api.RewardVideoAdRequest

class BigoRewardedAdapter(
    private val context: Context,
    private val placement: Int,
    private val unit: String,
    private val callbacks: RewardedCallbacks,
) : RewardedAdapter {
    private val tag = "BigoRewardedAdapter"
    private val network = "bigo"

    private var rewarded: RewardVideoAd? = null
    private var bid: AdBid? = null

    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        val price = bid?.price ?: 0.0

        return price
    }

    override fun load() {
        val ext = Settings.mediationParam

        val loader = RewardVideoAdLoader.Builder()
            .withExt(ext.toString())
            .withAdLoadListener(object : AdLoadListener<RewardVideoAd> {
                override fun onError(err: AdError) {
                    callbacks.onFailure(this@BigoRewardedAdapter, err.message)
                }

                override fun onAdLoaded(ad: RewardVideoAd) {
                    ad.setAdInteractionListener(object : RewardAdInteractionListener {
                        override fun onAdError(error: AdError) {
                            callbacks.onNoAd(this@BigoRewardedAdapter, error.message)
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
                                this@BigoRewardedAdapter,
                                revenue = revenue,
                                data = "",
                            )
                        }

                        override fun onAdClicked() {
                            callbacks.onClick(this@BigoRewardedAdapter)
                        }

                        override fun onAdOpened() {
                            callbacks.onOpen(this@BigoRewardedAdapter)
                        }

                        override fun onAdClosed() {
                            callbacks.onClose(this@BigoRewardedAdapter)
                        }

                        override fun onAdRewarded() {
                            callbacks.onReward(this@BigoRewardedAdapter, 0, "")
                        }
                    })

                    rewarded = ad
                    bid = ad.bid // cpm

                    callbacks.onLoad(this@BigoRewardedAdapter)
                }

            }).build()

        val request = RewardVideoAdRequest.Builder()
            .withSlotId(unit)
            .build()

        loader.loadAd(request)
    }

    override fun show(activity: Activity) {
        rewarded?.show(activity)
    }

    override fun win(price: Double, bidder: String) {
        bid?.notifyWin(price, bidder)
    }

    override fun loss(price: Double, bidder: String, reason: Int) {
        bid?.notifyLoss(price, bidder, reason)
    }

    override fun network(): String {
        return network
    }
}