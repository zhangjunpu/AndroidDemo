package com.junpu.topdf.widget

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*
import androidx.annotation.RequiresApi
import com.junpu.log.L

/**
 * 处理各种通知 & 请求事件
 * @author junpu
 * @date 2019-10-28
 */
open class H5WebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//        L.d("H5WebViewClient.shouldOverrideUrlLoading -> request?.url: ${request?.url}")
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
//        L.d("H5WebViewClient.shouldInterceptRequest -> request?.url: ${request?.url}")
        return super.shouldInterceptRequest(view, request)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        L.d("H5WebViewClient.onPageStarted -> url: $url")
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        L.d("H5WebViewClient.onPageFinished -> url: $url")
        super.onPageFinished(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        L.e("H5WebViewClient.onReceivedError -> error:  ${error?.errorCode}, ${error?.description}")
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        L.e("H5WebViewClient.onReceivedError -> error: ${errorCode}, $description")
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        L.e("H5WebViewClient.onReceivedHttpError -> request?.url: ${errorResponse?.data}")
        super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        L.e("H5WebViewClient.onReceivedSslError -> error: ${error?.url}")
        super.onReceivedSslError(view, handler, error)
    }
}