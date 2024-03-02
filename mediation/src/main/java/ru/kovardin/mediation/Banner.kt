package ru.kovardin.mediation

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerCallbacks
import ru.kovardin.mediation.interfaces.MediationBannerCallbacks
import ru.kovardin.mediation.services.PlacementResponse
import ru.kovardin.mediation.services.PlacementsHandler
import ru.kovardin.mediation.services.PlacementsService
import ru.kovardin.mediation.utils.CallbackAggregator

class Banner(private val id: String, private val callbacks: MediationBannerCallbacks) {
    private val lossReasonLowerThanFloorPrice = 100
    private val lossReasonLowerThanHighestPrice = 101

    private val tag = "Banner"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val placements = PlacementsService()
    private var bets = mutableMapOf<String, BannerAdapter>()

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

                        adapter.createBanner(context, placement = placement, unit = unit, callbacks = object : BannerCallbacks {
                            override fun onLoad(ad: BannerAdapter) {
                                bets[unit] = ad

                                callbacks.onLoad(ad)

                                aggregator.increment();
                            }

                            override fun onNoAd(ad: BannerAdapter, reason: String) {
                                callbacks.onNoAd(ad, reason)

                                aggregator.increment();
                            }

                            override fun onImpression(ad: BannerAdapter, revenue: Double, data: String) {
                                callbacks.onImpression(ad, revenue, data)
                            }

                            override fun onClick(ad: BannerAdapter) {
                                callbacks.onClick(ad)
                            }

                            override fun onFailure(ad: BannerAdapter?, message: String) {
                                callbacks.onFailure(ad, message)
                            }
                        }).load()
                    }
                }
            })
        }
    }

    fun view(context: Context): View {
        if (bets.isEmpty()) {
            return TextView(context)
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
        return ad.value.view(context)
    }
}