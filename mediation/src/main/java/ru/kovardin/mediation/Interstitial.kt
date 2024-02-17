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


class Interstitial(private val id: String, private val callbacks: InterstitialCallbacks) {
    private val lossReasonLowerThanFloorPrice = 100
    private val lossReasonLowerThanHighestPrice = 101

    private val tag = "Interstitial"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val placements = PlacementsService()
    private var bets = mutableMapOf<String, InterstitialAdapter>()

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

                        adapter.createInterstitial(context = context, placement = placement, unit = unit, callbacks = object : InterstitialCallbacks {
                            override fun onLoad(ad: InterstitialAdapter) {
                                bets[unit] = ad

                                callbacks.onLoad(ad)
                            }

                            override fun onNoAd(ad: InterstitialAdapter, reason: String) {
                                callbacks.onNoAd(ad, reason)
                            }

                            override fun onOpen(ad: InterstitialAdapter) {
                                callbacks.onOpen(ad)
                            }

                            override fun onImpression(ad: InterstitialAdapter, revenue: Double, data: String) {
                                callbacks.onImpression(ad, revenue, data)
                            }

                            override fun onClick(ad: InterstitialAdapter) {
                                callbacks.onClick(ad)
                            }

                            override fun onClose(ad: InterstitialAdapter) {
                                callbacks.onClose(ad)
                            }

                            override fun onFailure(ad: InterstitialAdapter?, message: String) {
                                callbacks.onFailure(ad, message)
                            }
                        }).load()
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