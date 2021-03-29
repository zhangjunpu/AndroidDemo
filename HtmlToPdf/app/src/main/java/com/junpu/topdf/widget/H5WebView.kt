package com.junpu.topdf.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.content.ContextCompat
import com.junpu.topdf.R

/**
 * WebView
 *
 * @author junpu
 * @date 2019-07-22
 */
class H5WebView : WebView {

    private var progressView: LinearProgressBar? = null // 进度条
    var isTouchable: Boolean = true
    var mark: String? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        progressView = LinearProgressBar(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 2f))
            progressColor = ContextCompat.getColor(context, R.color.colorAccent)
            progress = 10
        }
        addView(progressView)
        initWebSettings() // 初始化设置
        webChromeClient = MyWebChromeClient()
        webViewClient = H5WebViewClient()
    }

    /**
     * 初始化WebSetting
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebSettings() {
        // 默认是false 设置true允许和js交互
        settings?.apply {
            javaScriptEnabled = true

            //  WebSettings.LOAD_DEFAULT 如果本地缓存可用且没有过期则使用本地缓存，否加载网络数据 默认值
            //  WebSettings.LOAD_CACHE_ELSE_NETWORK 优先加载本地缓存数据，无论缓存是否过期
            //  WebSettings.LOAD_NO_CACHE  只加载网络数据，不加载本地缓存
            //  WebSettings.LOAD_CACHE_ONLY 只加载缓存数据，不加载网络数据
            // Tips:有网络可以使用LOAD_DEFAULT 没有网时用LOAD_CACHE_ELSE_NETWORK
//            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK;

            // 开启 DOM storage API 功能 较大存储空间，使用简单
            domStorageEnabled = true

            // 开启 Application Caches 功能 方便构建离线APP 不推荐使用
//            setAppCacheEnabled(true)
//            val cachePath = context.applicationContext.getDir("cache", Context.MODE_PRIVATE).path
//            setAppCachePath(cachePath)
//            setAppCacheMaxSize(5 * 1024 * 1024)

            // 设置数据库缓存路径 存储管理复杂数据 方便对数据进行增加、删除、修改、查询 不推荐使用
//            databaseEnabled = true
//            val dbPath = context.applicationContext.getDir("db", Context.MODE_PRIVATE).path
//            setDatabasePath(dbPath)

            // 设置自适应屏幕，两者合用
            useWideViewPort = true // 将图片调整到适合webview的大小
            loadWithOverviewMode = true // 缩放至屏幕的大小

            // 缩放操作
            setSupportZoom(true) // 支持缩放，默认为true。是下面那个的前提。
            builtInZoomControls = true // 设置内置的缩放控件。若为false，则该WebView不可缩放
            displayZoomControls = false // 隐藏原生的缩放控件

            // 其他细节操作
            javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口
            allowFileAccess = true // 设置可以访问文件
            loadsImagesAutomatically = true // 支持自动加载图片
            defaultTextEncodingName = "utf-8" // 设置编码格式
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // http与https混合模式
        }
    }

    /**
     * 辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。
     */
    private inner class MyWebChromeClient : H5WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            updateProgress(newProgress)
            super.onProgressChanged(view, newProgress)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isTouchable) return false
        return super.onTouchEvent(event)
    }

    /**
     * 更新ProgressView
     */
    fun updateProgress(progress: Int) {
        progressView?.setProgressByAnimator(progress)
        if (progress == 100) progressView?.visibility = View.INVISIBLE
    }

    /**
     * 能退则退
     */
    fun back(): Boolean {
        if (canGoBack()) {
            goBack()
            return true
        }
        return false
    }

    /**
     * 能进则仅
     */
    fun forward(): Boolean? {
        if (canGoForward()) {
            goForward()
            return true
        }
        return false
    }

    /**
     * 回收WebView
     */
    fun release(isClearCache: Boolean = false, isRemove: Boolean = false) {
        loadDataWithBaseURL("about:blank", "", "text/html", "utf-8", null)
        clearHistory()
        if (isClearCache) clearCache(true)
        pauseTimers()
        if (isRemove) (parent as? ViewGroup)?.removeView(this)
        destroy()
    }

    private fun dp2px(context: Context, dp: Float): Int {
        return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
    }
}
