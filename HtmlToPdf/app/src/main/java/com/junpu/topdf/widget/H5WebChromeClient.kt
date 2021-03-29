package com.junpu.topdf.widget

import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.junpu.log.L

/**
 * 辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。
 * @author junpu
 * @date 2019-10-28
 */
open class H5WebChromeClient : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        L.d("H5WebChromeClient.onProgressChanged -> newProgress: $newProgress")
        super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        L.d("H5WebChromeClient.onReceivedTitle -> title: $title")
        super.onReceivedTitle(view, title)
    }

    /**
     * 支持javascript的警告框
     */
    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return super.onJsAlert(view, url, message, result)
    }

    /**
     * 支持javascript的确认框
     */
    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return super.onJsConfirm(view, url, message, result)
    }

    /**
     * 支持javascript输入框
     */
    override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
        return super.onJsPrompt(view, url, message, defaultValue, result)
    }
}