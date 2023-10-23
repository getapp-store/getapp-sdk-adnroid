package ru.kovardin.billing.screens.boosty

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import ru.kovardin.boosty.Boosty
import ru.kovardin.boosty.SubscribeHandler
import ru.kovardin.boosty.Subscriber
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

    fun subscribe(context: Context) {
        Boosty.client.subscribe(object : SubscribeHandler {
            override fun onFailure(e: Throwable) {
                Log.e("BoostyViewModel", "error on subscriber ${e.message}")
            }

            override fun onSuccess(resp: Subscriber) {
                Log.d("BoostyViewModel", "subscriber: ${resp}")
                Toast.makeText(context, "${resp.subscription.name}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}