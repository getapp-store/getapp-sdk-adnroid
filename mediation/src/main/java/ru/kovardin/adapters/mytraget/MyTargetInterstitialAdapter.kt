package ru.kovardin.adapters.mytraget

import android.app.Activity
import android.content.Context
import android.util.Log
import com.my.target.ads.InterstitialAd
import com.my.target.ads.InterstitialAd.InterstitialAdListener
import com.my.target.common.models.IAdLoadingError
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


class MyTargetInterstitialAdapter(
    private val context: Context,
    private val placement: Int,
    private val unit: String,
    private val callbacks: InterstitialCallbacks,
) : InterstitialAdapter {
    private val tag = "MyTargetInterstitialAdapter"
    private val network = "mytarget"

    private var cpm: Double = 0.0
    private var bid: Double = 0.0

    private var interstitial: InterstitialAd? = null

    private val auction = AuctionService()
    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        return bid
    }

    override fun load() {
        interstitial = InterstitialAd(unit.toInt(), context)
        interstitial?.isMediationEnabled = true
        interstitial?.setListener(object : InterstitialAdListener {
            override fun onLoad(ad: InterstitialAd) {
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

                callbacks.onLoad(this@MyTargetInterstitialAdapter)
            }

            override fun onNoAd(reason: IAdLoadingError, ad: InterstitialAd) {
                callbacks.onNoAd(this@MyTargetInterstitialAdapter, reason.message)
            }

            override fun onClick(ad: InterstitialAd) {
                callbacks.onClick(this@MyTargetInterstitialAdapter)
            }
            override fun onDisplay(ad: InterstitialAd) {
                callbacks.onOpen(this@MyTargetInterstitialAdapter)

                scope.launch {
                    impressions.impression(
                        placement = placement,
                        data = ImpressionRequest(
                            unit = unit,
                            revenue = cpm / 1000,
                            data = "",
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

                callbacks.onImpression(this@MyTargetInterstitialAdapter, "")
            }

            override fun onDismiss(ad: InterstitialAd) {
                callbacks.onClose(this@MyTargetInterstitialAdapter)
            }
            override fun onVideoCompleted(ad: InterstitialAd) {}
        })

        interstitial?.load()
    }

    override fun show(activity: Activity) {
        interstitial?.show();
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