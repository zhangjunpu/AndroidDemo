package com.junpu.topdf

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.junpu.log.L
import com.junpu.toast.toast
import com.junpu.topdf.utils.PdfHelper
import kotlinx.android.synthetic.main.main_activity.*

/**
 * @author junpu
 * @date 2020/7/21
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val URL = "https://developer.android.google.cn/"
    }

    private var pdfHelper: PdfHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        getStorageCache()
        webView.run {
            setInitialScale(80)
            loadUrl(URL)
        }
        pdfHelper = PdfHelper(applicationContext).apply {
            doOnConvertCallback {
                toast(if (it) "success" else "failure")
            }
        }
        btnConvert.setOnClickListener {
            pdfHelper?.convertToPdf(webView)
        }
    }

    private fun getStorageCache() {
        L.ee(Environment.getExternalStorageDirectory())
        L.vv(externalCacheDir?.path)
        val path = externalCacheDir?.path?.run {
            substring(0, indexOf("/Android"))
        }
        L.vv(path)
    }

}