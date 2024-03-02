package ru.kovardin.adapters.mytraget

import android.app.Activity
import android.content.Context
import android.util.Log
import com.my.target.ads.Reward
import com.my.target.ads.RewardedAd
import com.my.target.common.models.IAdLoadingError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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


class MyTargetRewardedAdapter(
    private val context: Context,
    private val placement: Int,
    private val unit: String,
    private val callbacks: RewardedCallbacks,
) : RewardedAdapter {
    private val tag = "MyTargetRewardedAdapter"

    private var cpm: Double = 0.0
    private var bid: Double = 0.0

    private var rewarded: RewardedAd? = null

    private val auction = AuctionService()
    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        return bid
    }
    override fun load() {
        rewarded = RewardedAd(unit.toInt(), context)
        rewarded?.isMediationEnabled = true
        rewarded?.setListener(object : RewardedAd.RewardedAdListener {
            override fun onLoad(ad: RewardedAd) {
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

                callbacks.onLoad(this@MyTargetRewardedAdapter)
            }

            override fun onNoAd(reason: IAdLoadingError, ad: RewardedAd) {
                callbacks.onNoAd(this@MyTargetRewardedAdapter, reason.message)
            }

            override fun onClick(ad: RewardedAd) {
                callbacks.onClick(this@MyTargetRewardedAdapter)
            }

            override fun onDisplay(ad: RewardedAd) {
                callbacks.onOpen(this@MyTargetRewardedAdapter)

                val revenue = cpm / 1000

                scope.launch {
                    impressions.impression(
                        placement = placement,
                        data = ImpressionRequest(
                            unit = unit,
                            revenue = revenue,
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

                callbacks.onImpression(
                    this@MyTargetRewardedAdapter,
                    revenue = revenue,
                    data = "",
                )
            }

            override fun onDismiss(ad: RewardedAd) {
                callbacks.onClose(this@MyTargetRewardedAdapter)
            }

            override fun onReward(r: Reward, ad: RewardedAd) {
                callbacks.onReward(this@MyTargetRewardedAdapter, 0, r.type)
            }
        })

        rewarded?.load()
    }

    override fun show(activity: Activity) {
        rewarded?.show();
    }

    override fun win(price: Double, bidder: String) {
        Log.i(tag, "win price: $price, bidder: $bidder")
    }

    override fun loss(price: Double, bidder: String, reason: Int) {
        Log.i(tag, "loss price: $price, bidder: $bidder, reason: $reason")
    }

    override fun network(): String {
        return MyTargetAdapter.NETWORK
    }
}