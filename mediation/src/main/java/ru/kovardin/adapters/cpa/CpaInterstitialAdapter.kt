package ru.kovardin.adapters.cpa

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
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


class CpaInterstitialAdapter(
    private val placement: Int,
    private val unit: String,
    private val callbacks: InterstitialCallbacks,
) : InterstitialAdapter {
    private val tag = "CpaInterstitialAdapter"
    private val network = "cpa"

    private var cpm: Double = 0.0
    private var bid: Double = 0.0

    private val auction = AuctionService()
    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        return bid
    }

    override fun load(context: Context) {
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

        // load price from server
        Log.i(tag, "load cpa banner")

        // get ad from server
        callbacks.onLoad(this@CpaInterstitialAdapter)
    }

    override fun show(activity: Activity) {
        // start your activity by passing the intent
        activity.startActivity(Intent(activity, InterstitialActivity::class.java).apply {
            // you can add values(if any) to pass to the next class or avoid using `.apply`
            putExtra("keyIdentifier", "")
        })

        scope.launch {
            impressions.impression(
                placement = placement,
                data = ImpressionRequest(
                    unit = unit,
                    data = "",
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

        callbacks.onImpression(this, "")
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