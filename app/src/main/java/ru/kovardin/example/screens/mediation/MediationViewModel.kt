package ru.kovardin.example.screens.mediation

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import ru.kovardin.mediation.Interstitial
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks

class MediationViewModel : ViewModel() {

    private val tag = "MediationViewModel"
    private var interstitial: Interstitial? = null
    fun init() {
        interstitial = Interstitial()
        interstitial?.init("1", callbacks = object : InterstitialCallbacks {
            override fun onLoad(ad: InterstitialAdapter) {
                Log.d(tag, "on load")
            }

            override fun onImpression(ad: InterstitialAdapter, data: String) {
                Log.d(tag, data)
            }

            override fun onFailure(message: String) {
                Log.e(tag, message)
            }
        })
    }
    fun load(context: Context) {
        interstitial?.load(context)
    }

    fun show(context: Activity) {
        interstitial?.show(context)
    }
}