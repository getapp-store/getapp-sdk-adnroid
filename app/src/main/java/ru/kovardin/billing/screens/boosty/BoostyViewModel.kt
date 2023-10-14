package ru.kovardin.billing.screens.boosty

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import ru.kovardin.boosty.Boosty
import ru.kovardin.boosty.Subscription
import ru.kovardin.boosty.SubscriptionsHandler
import ru.kovardin.boosty.SubscriptionsResponse

class BoostyViewModel: ViewModel() {
    val subscriptions = mutableStateListOf<Subscription>()

    fun fetch() {
        subscriptions.clear()

        Boosty.client.subscriptions(object : SubscriptionsHandler {
            override fun onFailure(e: Throwable) {
                Log.e("BoostyViewModel", e.message.toString())
            }

            override fun onSuccess(resp: SubscriptionsResponse) {
                Log.d("BoostyViewModel", resp.toString())
                subscriptions.addAll(resp.items)
            }
        })
    }

    fun subscribe(id: String) {
        Boosty.client.subscribe()
    }
}