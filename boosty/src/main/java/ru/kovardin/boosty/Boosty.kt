package ru.kovardin.boosty

import android.content.Context
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.net.URL

class Boosty {

    fun login(context: Context, url: String) {
        val web = WebView(context)
        val sheet = BottomSheetDialog(context)
        val view = LinearLayout(context)

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


        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                val url = request!!.url.toString()

//                request.requestHeaders.set("User-Agent", "Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Mobile Safari/537.36")
//                request.requestHeaders.set("Sec-Ch-Ua-Mobile", "?1")
//                request.requestHeaders.set("Sec-Ch-Ua-Platform", "Android")
//                request.requestHeaders.set("Sec-Ch-Ua-Platform", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"")
//                request.requestHeaders.set("Sec-Fetch-Site", "none")
//                request.requestHeaders.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
//                request.requestHeaders.set("Pragma", "no-cache")
//                request.requestHeaders.set("X-App", "mobile")

                println(request.requestHeaders)

                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(w: WebView, u: String): Boolean {
                w.loadUrl(u)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                println(url)

            }
        }


        val height = (context.resources.displayMetrics.heightPixels / 1.5).toInt()
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
}