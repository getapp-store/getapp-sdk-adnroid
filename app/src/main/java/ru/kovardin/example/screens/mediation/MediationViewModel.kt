package ru.kovardin.example.screens.mediation

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import ru.kovardin.mediation.Banner
import ru.kovardin.mediation.Interstitial
import ru.kovardin.mediation.interfaces.BannerAdapter
import ru.kovardin.mediation.interfaces.BannerlCallbacks
import ru.kovardin.mediation.interfaces.InterstitialAdapter
import ru.kovardin.mediation.interfaces.InterstitialCallbacks

class MediationViewModel : ViewModel() {

    private val tag = "MediationViewModel"
    private var interstitial: Interstitial? = null
    private var banner: Banner? = null

    fun init(context: Context) {
        interstitial = Interstitial( "1", callbacks = object : InterstitialCallbacks {
            override fun onLoad(ad: InterstitialAdapter) {
                Log.d(tag, "onLoad: $ad")
            }

            override fun onImpression(ad: InterstitialAdapter, data: String) {
                Log.d(tag, "onImpression: $ad, $data")
            }

            override fun onClick(ad: InterstitialAdapter) {
                Log.d(tag, "onClick: $ad")
            }

            override fun onClose(ad: InterstitialAdapter) {
                Log.d(tag, "onClose: $ad")
            }

            override fun onNoAd(ad: InterstitialAdapter, reason: String) {
                Log.d(tag, "no ad")
            }

            override fun onOpen(ad: InterstitialAdapter) {
                Log.d(tag, "onOpen: $ad")
            }

            override fun onFailure(ad: InterstitialAdapter?, message: String) {
                Log.e(tag, message)
            }
        })

//        interstitial?.load(context)

        banner = Banner("2", callbacks = object : BannerlCallbacks{
            override fun onLoad(ad: BannerAdapter) {
            }

            override fun onNoAd(ad: BannerAdapter, reason: String) {
            }

            override fun onImpression(ad: BannerAdapter, data: String) {
            }

            override fun onClick(ad: BannerAdapter) {
            }

            override fun onFailure(ad: BannerAdapter?, message: String) {
            }

        })
    }

    fun loadBanner(context: Context) {
        banner?.load(context)
    }

    fun showBanner(context: Context) : View {
        return banner?.view(context) ?: TextView(context)
    }

    fun loadInterstitial(context: Context) {
        interstitial?.load(context)
    }

    fun showInterstitial(context: Activity) {
        interstitial?.show(context)
    }
}