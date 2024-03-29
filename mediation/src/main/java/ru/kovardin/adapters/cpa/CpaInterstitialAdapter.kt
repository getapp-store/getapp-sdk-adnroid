package ru.kovardin.adapters.cpa

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.adapters.cpa.view.InterstitialActivity
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
    private val context: Context,
    private val placement: Int,
    private val unit: String,
    private val callbacks: InterstitialCallbacks,
) : InterstitialAdapter {
    private val tag = "CpaInterstitialAdapter"

    private var cpm: Double = 0.0
    private var bid: Double = 0.0

    private val auction = AuctionService()
    private val impressions = ImpressionsService()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun bid(): Double {
        return bid
    }

    override fun load() {
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

        Log.i(tag, "load cpa banner")

        callbacks.onLoad(this@CpaInterstitialAdapter)
    }

    override fun show(activity: Activity) {
        activity.startActivity(Intent(activity, InterstitialActivity::class.java).apply {
            putExtra("keyIdentifier", "")
        })

        val revenue = cpm / 1000

        scope.launch {
            impressions.impression(
                placement = placement,
                data = ImpressionRequest(
                    unit = unit,
                    revenue = revenue,
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
            this,
            revenue = revenue,
            data = "",
        )
    }


    override fun win(price: Double, bidder: String) {
        Log.i(tag, "win price: $price, bidder: $bidder")
    }

    override fun loss(price: Double, bidder: String, reason: Int) {
        Log.i(tag, "loss price: $price, bidder: $bidder, reason: $reason")
    }

    override fun network(): String {
        return CpaAdapter.NETWORK
    }
}