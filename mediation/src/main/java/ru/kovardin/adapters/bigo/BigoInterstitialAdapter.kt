package ru.kovardin.adapters.bigo

import android.app.Activity
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks
import ru.kovardin.mediation.services.ImpressionHandler
import ru.kovardin.mediation.services.ImpressionRequest
import ru.kovardin.mediation.services.ImpressionsService
import sg.bigo.ads.api.AdBid
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdInteractionListener
import sg.bigo.ads.api.AdLoadListener
import sg.bigo.ads.api.InterstitialAd
import sg.bigo.ads.api.InterstitialAdLoader
import sg.bigo.ads.api.InterstitialAdRequest


class BigoInterstitialAdapter(
    private val placement: Int,
    private val unit: String,
    private val callbacks: InterstitialCallbacks,
) : InterstitialAdapter {
    private val tag = "BigoInterstitialAdapter"
    private val network = "bigo"

    private var interstitial: InterstitialAd? = null
    private var bid: AdBid? = null

    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        val price = bid?.price ?: 0.0

        return price
    }

    override fun load(context: Context) {
        val ext = JSONObject()
        ext.putOpt("mediationName", "getapp"); // admob、meta etc.
        ext.putOpt("mediationVersion", "1.0.0"); // 12.1.0
        ext.putOpt("adapterVersion", "1.0.0"); // 12.1.0.0

        val loader: InterstitialAdLoader = InterstitialAdLoader.Builder()
            .withExt(ext.toString())
            .withAdLoadListener(object : AdLoadListener<InterstitialAd> {
                override fun onError(err: AdError) {
                    callbacks.onFailure(this@BigoInterstitialAdapter, err.message)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    ad.setAdInteractionListener(object : AdInteractionListener {
                        override fun onAdError(error: AdError) {
                            callbacks.onNoAd(this@BigoInterstitialAdapter, error.message)
                        }

                        override fun onAdImpression() {
                            scope.launch {
                                impressions.impression(
                                    placement = placement,
                                    data = ImpressionRequest(
                                        unit = unit,
                                        data = "",
                                        revenue = (bid?.price ?: 0.0) / 1000 // цена одного показа
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

                            callbacks.onImpression(this@BigoInterstitialAdapter, "")
                        }

                        override fun onAdClicked() {
                            callbacks.onClick(this@BigoInterstitialAdapter)
                        }

                        override fun onAdOpened() {
                            callbacks.onOpen(this@BigoInterstitialAdapter)
                        }

                        override fun onAdClosed() {
                            callbacks.onClose(this@BigoInterstitialAdapter)
                        }
                    })

                    interstitial = ad
                    bid = ad.bid // cpm

                    callbacks.onLoad(this@BigoInterstitialAdapter)
                }

            }).build()

        val request = InterstitialAdRequest.Builder()
            .withSlotId(unit)
            .build()

        loader.loadAd(request)
    }

    override fun show(activity: Activity) {
        interstitial?.show(activity)
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