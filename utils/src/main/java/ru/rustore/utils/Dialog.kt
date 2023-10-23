package ru.kovardin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("SetJavaScriptEnabled")
class Dialog(val context: Context, val url: String) {

    lateinit var client: WebViewClient
    private var web: WebView = WebView(context)
    private var sheet: BottomSheetDialog = BottomSheetDialog(context)
    private var view: LinearLayout = LinearLayout(context)

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
//        web.settings.useWideViewPort = false

//        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);

        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);

        web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web.setScrollbarFadingEnabled(false);

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

        sheet.setContentView(view)
        sheet.show()
    }

    fun close() {
        view.removeView(web)
        web.removeAllViews()
        web.destroy()
        sheet.hide()
    }
}