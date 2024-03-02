package ru.kovardin.mediation

import android.app.Activity
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.mediation.interfaces.MediationRewardedCallbacks
import ru.kovardin.mediation.interfaces.RewardedAdapter
import ru.kovardin.mediation.interfaces.RewardedCallbacks
import ru.kovardin.mediation.services.PlacementResponse
import ru.kovardin.mediation.services.PlacementsHandler
import ru.kovardin.mediation.services.PlacementsService
import ru.kovardin.mediation.utils.CallbackAggregator

class Rewarded(private val id: String, private val callbacks: MediationRewardedCallbacks) {
    private val lossReasonLowerThanFloorPrice = 100
    private val lossReasonLowerThanHighestPrice = 101

    private val tag = "Rewarded"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val placements = PlacementsService()
    private var bets = mutableMapOf<String, RewardedAdapter>()

    fun load(context: Context) {
        bets.clear()

        scope.launch {
            placements.get(id, object : PlacementsHandler {
                override fun onFailure(e: Throwable) {
                    Log.e(tag, e.message.toString())
                }

                override fun onSuccess(resp: PlacementResponse) {
                    // бежим по всем адаптерам и получаем токены для бидинга
                    val units = resp.units.filter {
                        Mediation.instance.adapters.contains(it.network)
                    }

                    val aggregator = CallbackAggregator(units.size)
                    aggregator.final = {
                        callbacks.onFinish()
                    }

                    for (u in units) {

                        val unit = u.unit
                        val placement = u.placement
                        val network = u.network

                        val adapter = Mediation.instance.adapters[network] ?: continue

                        adapter.createRewarded(context = context, placement = placement, unit = unit, callbacks = object : RewardedCallbacks {
                            override fun onLoad(ad: RewardedAdapter) {
                                bets[unit] = ad

                                callbacks.onLoad(ad)

                                aggregator.increment()
                            }

                            override fun onNoAd(ad: RewardedAdapter, reason: String) {
                                callbacks.onNoAd(ad, reason)

                                aggregator.increment()
                            }

                            override fun onOpen(ad: RewardedAdapter) {
                                callbacks.onOpen(ad)
                            }

                            override fun onImpression(ad: RewardedAdapter, revenue: Double, data: String) {
                                callbacks.onImpression(ad, revenue, data)
                            }

                            override fun onClick(ad: RewardedAdapter) {
                                callbacks.onClick(ad)
                            }

                            override fun onClose(ad: RewardedAdapter) {
                                callbacks.onClose(ad)
                            }

                            override fun onReward(ad: RewardedAdapter, amount: Int, type: String) {
                                callbacks.onReward(ad, amount, type)
                            }

                            override fun onFailure(ad: RewardedAdapter?, message: String) {
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