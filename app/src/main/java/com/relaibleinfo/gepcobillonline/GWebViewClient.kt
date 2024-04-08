package com.relaibleapps.candyland

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import java.lang.ref.WeakReference

/**
 * 类描述：默认WebViewClient
 */
open class GWebViewClient(
    private val activity: WeakReference<H5Activity>,
) : WebViewClient() {
    private var currentUrl: String? = null
    override fun onPageFinished(webView: WebView, url: String?) {
        super.onPageFinished(webView, url)
        //        LogUtils.i("onPageFinished: $url ")
        if (currentUrl != url) {
            //                    LogUtils.i("injectCode")
            WebUtils.injectCode(webView)
            currentUrl = url
        }
    }

    override fun onReceivedSslError(webView: WebView, sslErrorHandler: SslErrorHandler, sslError: SslError) {
        if (activity.get() == null) return
        //        LogUtils.i("onReceivedSslError: ${sslError.url}")
        val builder = AlertDialog.Builder(activity.get())
        builder.setMessage("The SSL authentication fails. Do you want to continue the access?")
        builder.setPositiveButton(
            "continue"
        ) { _, _ ->
            sslErrorHandler.proceed()
        }
        builder.setNegativeButton(
            "cancel"
        ) { _, _ ->
            sslErrorHandler.cancel()
        }
        builder.create().show()
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        //        LogUtils.i("shouldOverrideUrlLoading: $url")
        if (url.isNullOrBlank() || url.isStartsWithHttp()) return false

        val containerActivity = activity.get() ?: return false

        val intent: Intent?
        // 执行URI的通用解析以将其转换为Intent。
        try {
            intent = when {
                url.startsWith("android-app://") ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        Intent.parseUri(url, Intent.URI_ANDROID_APP_SCHEME)
                    } else {
                        Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    }

                url.startsWith("intent://") ->
                    Intent.parseUri(url, Intent.URI_INTENT_SCHEME)

                else ->
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
            }
        } catch (e: Exception) {
            //            LogUtils.e(e.toString())
            return true
        }
        if (intent == null) return true

        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        try {
            containerActivity.startActivityIfNeeded(intent, -1)
            return true
        } catch (e: Exception) {
            //            LogUtils.e(e)
        }
        return true
    }


}