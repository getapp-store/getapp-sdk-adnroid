package ru.kovardin.adapters.mytraget

import android.content.Context
import android.util.Log
import android.view.View
import com.my.target.ads.MyTargetView
import com.my.target.common.models.IAdLoadingError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

class MyTargetBannerAdapter(
    private val context: Context,
    private val placement: Int,
    private val unit: String,
    private val callbacks: BannerCallbacks,
) : BannerAdapter {
    private val tag = "MyTargetBannerAdapter"

    val banner = MyTargetView(context);

    private var cpm: Double = 0.0
    private var bid: Double = 0.0

    private val auction = AuctionService()
    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        return bid
    }

    override fun load() {
        banner.setRefreshAd(false)
        banner.setSlotId(unit.toInt())
        banner.listener = object : MyTargetView.MyTargetViewListener {
            override fun onLoad(v: MyTargetView) {
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

                callbacks.onLoad(this@MyTargetBannerAdapter)
            }

            override fun onNoAd(err: IAdLoadingError, v: MyTargetView) {
                callbacks.onNoAd(this@MyTargetBannerAdapter, err.message)
            }

            override fun onShow(v: MyTargetView) {
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
                    this@MyTargetBannerAdapter,
                    revenue = revenue,
                    data = "",
                )
            }

            override fun onClick(v: MyTargetView) {
                callbacks.onClick(this@MyTargetBannerAdapter)
            }

        }

        banner.load()
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
        return MyTargetAdapter.NETWORK
    }
}