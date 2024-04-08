package com.relaibleapps.candyland

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.relaibleapps.candyland.WebUtils.setWeb
import java.lang.ref.WeakReference


/**
 * 项目名称：WebContainer
 * 类描述：
 * 创建人：liujun
 * 创建时间：2023/2/14 05:01
 * 修改备注：
 * @version
 */
class H5Activity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar

    // 用于处理文件选择请求的回调
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    private val getFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val url = it?.data?.data
        fileChooserCallback?.onReceiveValue(if (url == null) null else arrayOf(url))
        fileChooserCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toUrl = intent.getStringExtra(url_key)
        val ua = intent.getStringExtra(ua_key)
        val jsName = intent.getStringExtra(js_name_key)
        val supportMultipleWindows = intent.getBooleanExtra(supportMultipleWindows_key, true)
        //        LogUtils.i("toUrl：$toUrl ua：$ua jsName：$jsName")
        if (toUrl.isNullOrBlank()) {
            finish()
            return
        }
        //        window.setFlags(
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //            WindowManager.LayoutParams.FLAG_FULLSCREEN
        //        )
        //
        //        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        //
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        //            window.attributes.layoutInDisplayCutoutMode =
        //                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        //            window.decorView.systemUiVisibility =
        //                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        //        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        webView = WebView(this)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        addContentView(
            webView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        addContentView(progressBar, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 18))
        setWeb(this, webView, ua, supportMultipleWindows)
        webView.setDownloadListener { url, _, _, _, _ ->
            val uri = Uri.parse(url)
            Intent(Intent.ACTION_VIEW, uri).apply {
                startActivity(this)
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            //            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            //                LogUtils.i("onConsoleMessage ${consoleMessage?.message()}")
            //                return super.onConsoleMessage(consoleMessage)
            //            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (view == null) return
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
            }

            override fun onShowFileChooser(
                webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = filePathCallback
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getFile.launch(fileChooserParams?.createIntent())
                } else {
                    getFile.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    })
                }
                return true
            }

            override fun onJsAlert(webView: WebView?, url: String?, message: String?, jsResult: JsResult): Boolean {
                if (isFinishing) return true
                val builder: AlertDialog.Builder = AlertDialog.Builder(
                    this@H5Activity
                )
                builder.setMessage(message).setPositiveButton(android.R.string.ok) { arg0, _ ->
                    arg0.dismiss()
                }.show()
                jsResult.cancel()
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWindow = WebView(this@H5Activity)
                newWindow.webViewClient = object : WebViewClient() {

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                        //                        LogUtils.i("shouldOverrideUrlLoading：${url}")
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory(Intent.CATEGORY_BROWSABLE)
                        startActivity(intent)
                        return true
                    }
                }
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWindow
                resultMsg.sendToTarget()
                return true
            }
        }
        val each = jsName?.split(",")?.filter { it.isNotNullOrBlank() }
        if (each.isNullOrEmpty()) {
            //            LogUtils.i("jsName is null")
        } else {
            each.forEach { name ->
                //                LogUtils.i("addJavascriptInterface：$name")
                webView.addJavascriptInterface(JSEvent(WeakReference(this)), name)
            }
        }
        webView.loadUrl(toUrl)
    }


    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack() // 返回前一个页面
            return
        }
        return super.onBackPressed()
    }


}

const val url_key = "nbse"

const val ua_key = "khnkjw"

const val js_name_key = "nbwbw"

const val supportMultipleWindows_key = "sdakns"

fun openH5(
    context: Context,
    url: String? = "",
    ua: String? = null,
    jsName: String? = null,
    supportMultipleWindows: Boolean = true
) {
    if (url.isNullOrBlank()) return
    val intent = Intent(context, H5Activity::class.java)
    intent.putExtra(url_key, url)
    ua?.let {
        intent.putExtra(ua_key, it)
    }
    jsName?.let {
        intent.putExtra(js_name_key, it)
    }
    intent.putExtra(supportMultipleWindows_key, supportMultipleWindows)
    context.startActivity(intent)
}