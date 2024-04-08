package com.relaibleapps.candyland

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import com.appsflyer.AppsFlyerLib
import com.game.BuildConfig
import com.relaibleapps.candyland.UtilsManager.appsFlyerEnable
import org.json.JSONObject
import java.lang.ref.WeakReference


/**
 * 项目名称：slot
 * 类描述：
 * 创建人：liujun
 * 创建时间：2023/2/18 16:40
 * 修改备注：
 * @version
 */
class JSEvent(private val activity: WeakReference<H5Activity>) {

    @JavascriptInterface
    fun jksd(eventType: String, data: String) {
        val h5Activity = activity.get() ?: return
        val map: Map<String, Any> = parseJsonToMap(data)
        //        LogUtils.i("appsEvent: $eventType  data: $data  map: $map")

        if (eventType.isNotNullOrEmpty()) { //appsflyer事件
            if (appsFlyerEnable) {
                AppsFlyerLib.getInstance().logEvent(h5Activity, eventType, map)
            }
        }
    }

    /**
     * 跳转系统浏览器
     */
    @JavascriptInterface
    fun nmmnwe(url: String) {
        //        LogUtils.i("browser url:$url")
        val h5Activity = activity.get() ?: return
        if (url.isBlank()) return

        val uri = Uri.parse(url)
        Intent(Intent.ACTION_VIEW, uri).apply {
            h5Activity.startActivity(this)
        }
    }

    /**
     * 跳转内置浏览器
     */
    @JavascriptInterface
    fun bnmnw(url: String) {
        //        LogUtils.i("newPage url:$url")
        val h5Activity = activity.get() ?: return
        if (url.isBlank()) return
        openH5(h5Activity, url)
    }

    /**
     * 获取 AppsFlyerUID
     */
    @JavascriptInterface
    fun qwesa(): String {
        val h5Activity = activity.get() ?: return ""
        val appsFlyerUID = AppsFlyerLib.getInstance().getAppsFlyerUID(h5Activity) ?: ""
        //        LogUtils.i("getAFI: $appsFlyerUID ")
        return appsFlyerUID
    }

    /**
     * 获取 gaid
     */
    @JavascriptInterface
    fun abcs(): String {
        //        LogUtils.i("getGad: ${WebUtils.gaid} ")
        return WebUtils.gaid
    }

    /**
     * 获取 App 包名
     */
    @JavascriptInterface
    fun vbsa(): String {
        val packageName = BuildConfig.APPLICATION_ID
        //        LogUtils.i("getPackage: $packageName")
        return packageName
    }

    /**
     * 获取 App 版本名
     */
    @JavascriptInterface
    fun mklw(): String {
        val versionName = BuildConfig.VERSION_NAME
        //        LogUtils.i("getAppVer: $versionName")
        return versionName
    }

    /**
     * 获取 App 版本号
     */
    @JavascriptInterface
    fun nbqw(): Int {
        val versionCode = BuildConfig.VERSION_CODE
        //        LogUtils.i("getAppCode: $versionCode")
        return versionCode
    }

    /**
     * 关闭当前页面
     */
    @JavascriptInterface
    fun bvnh() {
        //        LogUtils.i("close")
        activity.get()?.finish()
    }

    //横竖屏切换
    @JavascriptInterface
    fun jhqwe(orientation: Int) {
        //        LogUtils.i("setOrien: $orientation")
        activity.get()?.requestedOrientation = orientation
    }

}


fun parseJsonToMap(jsonString: String): Map<String, Any> {
    val map: MutableMap<String, Any> = mutableMapOf()
    try {
        val jsonObject = JSONObject(jsonString)
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.opt(key)

            if (value is JSONObject) {
                map[key] = parseJsonToMap(value.toString())
            } else if (value != null) {
                map[key] = value
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return map
}

fun parseJsonToMapString(jsonString: String): Map<String, String> {
    val map: MutableMap<String, String> = mutableMapOf()
    try {
        val jsonObject = JSONObject(jsonString)
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.optString(key)
            if (value.isNotNullOrBlank()) {
                map[key] = value
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return map
}