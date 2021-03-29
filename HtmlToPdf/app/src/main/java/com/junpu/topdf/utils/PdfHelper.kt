package com.junpu.topdf.utils

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintAttributes.Resolution
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentAdapter.LayoutResultCallback
import android.print.PrintDocumentAdapter.WriteResultCallback
import android.webkit.WebView
import com.android.dx.stock.ProxyBuilder
import com.junpu.log.L
import com.junpu.log.logStackTrace
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationHandler

/**
 * HTML转PDF
 *
 * @author junpu
 * @date 2020/7/21
 */
class PdfHelper(private val context: Context) {

    companion object {
        private const val LAYOUT_FINISHED = "onLayoutFinished"
        private const val WRITE_FINISHED = "onWriteFinished"
    }

    private val file by lazy { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/pdf.pdf") }
    private val dexCacheFile by lazy { context.getDir("dex", Context.MODE_PRIVATE) }

    // 获取需要打印的WebView适配器
    private var printAdapter: PrintDocumentAdapter? = null
    private var descriptor: ParcelFileDescriptor? = null
    private val ranges by lazy { arrayOf(PageRange.ALL_PAGES) }

    // 回调
    private var callback: ((isSuccess: Boolean) -> Unit)? = null

    /**
     * 转换回调
     */
    fun doOnConvertCallback(callback: ((isSuccess: Boolean) -> Unit)?) {
        this.callback = callback
    }

    /**
     * WebView to pdf
     */
    fun convertToPdf(webView: WebView) {
        // android 5.0之后，出于对动态注入字节码安全性德考虑，已经不允许随意指定字节码的保存路径了，需要放在应用自己的包名文件夹下。
        // 新的创建DexMaker缓存目录的方式，直接通过context获取路径
        if (!dexCacheFile.exists()) {
            dexCacheFile.mkdir()
        }
        try {
            //创建待写入的PDF文件，pdfFilePath为自行指定的PDF文件路径
            if (file?.exists() == true) {
                file?.delete()
            }
            file?.createNewFile()
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)

            // 设置打印参数
            val attributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4) // 尺寸A4
                .setResolution(Resolution("id", Context.PRINT_SERVICE, 300, 300)) // 分辨率
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
            //打印所有界面
            printAdapter = webView.createPrintDocumentAdapter()
            // 开始打印
            printAdapter?.onStart()
            printAdapter?.onLayout(
                attributes,
                attributes,
                CancellationSignal(),
                getLayoutResultCallback(InvocationHandler { _, method, _ ->
                    // 监听到内部调用了onLayoutFinished()方法，即打印成功
                    if (method.name == LAYOUT_FINISHED) {
                        onLayoutSuccess()
                    } else {
                        // 监听到打印失败或者取消了打印
                        callback?.invoke(false)
                    }
                    null
                }, dexCacheFile.absoluteFile), Bundle()
            )
        } catch (e: IOException) {
            e.logStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun onLayoutSuccess() {
        // 写入文件到本地
        printAdapter?.onWrite(
            ranges,
            descriptor,
            CancellationSignal(),
            getWriteResultCallback(InvocationHandler { _, method, _ ->
                // PDF文件写入本地完成，导出成功
                if (method.name == WRITE_FINISHED) {
                    L.vv("导出成功")
                    callback?.invoke(true)
                } else {
                    callback?.invoke(false)
                }
                null
            }, dexCacheFile.absoluteFile)
        )
    }

    @Throws(IOException::class)
    private fun getLayoutResultCallback(
        invocationHandler: InvocationHandler?,
        dexCacheDir: File?
    ): LayoutResultCallback {
        return ProxyBuilder.forClass(LayoutResultCallback::class.java)
            .dexCache(dexCacheDir)
            .handler(invocationHandler)
            .build()
    }

    @Throws(IOException::class)
    private fun getWriteResultCallback(
        invocationHandler: InvocationHandler?,
        dexCacheDir: File?
    ): WriteResultCallback {
        return ProxyBuilder.forClass(WriteResultCallback::class.java)
            .dexCache(dexCacheDir)
            .handler(invocationHandler)
            .build()
    }
}