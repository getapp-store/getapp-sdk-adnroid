package ru.kovardin.mediation

import android.app.Activity
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks
import ru.kovardin.mediation.services.PlacementsHandler
import ru.kovardin.mediation.services.PlacementResponse
import ru.kovardin.mediation.services.PlacementsService


class Interstitial {
    private val lossReasonLowerThanFloorPrice = 100
    private val lossReasonLowerThanHighestPrice = 101

    private val tag = "Interstitial"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val placements = PlacementsService()

    // private var ad: InterstitialAdapter? = null
    private lateinit var callbacks: InterstitialCallbacks
    private lateinit var id: String

    private var bets = mutableMapOf<String, InterstitialAdapter>()

    fun init(id: String, callbacks: InterstitialCallbacks) {
        this.callbacks = callbacks
        this.id = id
    }

    fun load(context: Context) {
        bets.clear()

        scope.launch {
            placements.get(id, object : PlacementsHandler {
                override fun onFailure(e: Throwable) {
                    Log.e(tag, e.message.toString())
                }

                override fun onSuccess(resp: PlacementResponse) {
                    // бежим по всем адаптерам и получаем токены для бидинга
                    for (u in resp.units) {

                        val unit = u.unit
                        val placement = u.placement
                        val network = u.network

                        val adapter = Mediation.instance.adapters[network] ?: continue

                        adapter.createInterstitial(placement = placement, unit = unit, callbacks = object : InterstitialCallbacks {
                            override fun onLoad(ad: InterstitialAdapter) {
                                bets[unit] = ad

                                callbacks.onLoad(ad)
                            }

                            override fun onImpression(ad: InterstitialAdapter, data: String) {
                                callbacks.onImpression(ad, data)
                            }

                            override fun onFailure(message: String) {
                                callbacks.onFailure(message)
                            }
                        }).load(context)
                    }
                }

            })
        }
    }

    fun show(activity: Activity) {
        if (bets.isEmpty()) {
            return
        }

        val ad = bets.toSortedMap().maxBy { it.value.bid() }

        bets.map {
            if (it.key != ad.key) {
                // сообщаем всем остальным почему проиграли
                it.value.loss(ad.value.bid(), ad.value.network(), lossReasonLowerThanHighestPrice)
            }
        }

        // отмечаем победителя
        ad.value.win(ad.value.bid(), ad.value.network())

        // показываем рекламу
        ad.value.show(activity)
    }
}