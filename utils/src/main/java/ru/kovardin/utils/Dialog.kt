package ru.kovardin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("SetJavaScriptEnabled")
class Dialog(val context: Context, val url: String) {
    lateinit var client: WebViewClient
    private var web: ObservableWebView = ObservableWebView(context)
    private var sheet: BottomSheetDialog = BottomSheetDialog(context)
    private var view: LinearLayout = LinearLayout(context)
    private var scroll = 0

    init {
        web.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )
        web.loadData("<html><body><h1>Loading...</h1></body></html>", "text/HTML", "UTF-8")
        web.settings.javaScriptEnabled = true
        web.settings.userAgentString = "Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Mobile Safari/537.36"
//        web.settings.useWideViewPort = true
//        web.settings.loadWithOverviewMode = true;
        web.settings.setSupportZoom(true);
        web.settings.builtInZoomControls = true;
        web.settings.displayZoomControls = false;

        web.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY;
        web.isScrollbarFadingEnabled = false;
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        sheet.setOnDismissListener(listener)
    }

    fun open() {
        web.webViewClient = client

        val height = context.resources.displayMetrics.heightPixels
        view.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            height
        )
        view.setPadding(0, 32, 0, 0)
        view.addView(web)

        web.loadUrl(url)

        sheet.behavior.setPeekHeight((height * 0.75).toInt())
        sheet.setContentView(view)
        sheet.show()

        // настройка скрола для контента внутри webview
        // https://medium.com/@nishantpardamwar/using-webview-with-bottomsheetdialog-f38e45cc95a5
        sheet.behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING && scroll > 0) {
                    // this is where we check if webview can scroll up or not and based on that we let BottomSheet close on scroll down
                    sheet.behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    close()
                }
            }
        })

        web.onScrollChangedCallback = object : ObservableWebView.OnScrollChangeListener {
            override fun onScrollChanged(
                currentHorizontalScroll: Int, currentVerticalScroll: Int,
                oldHorizontalScroll: Int, oldcurrentVerticalScroll: Int
            ) {
                scroll = currentVerticalScroll

                Log.d("mCurrentWebViewScrollY", scroll.toString())
            }
        }
    }

    fun close() {
        view.removeView(web)
        web.removeAllViews()
        web.destroy()
        sheet.hide()
    }
}