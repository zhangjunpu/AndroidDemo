package com.junpu.record.demo

import android.app.Application
import com.junpu.common.CacheData
import com.junpu.log.L
import com.junpu.record.RecordCache
import com.junpu.toast.toastContext
import java.io.File

/**
 *
 * @author junpu
 * @date 2021/3/29
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        toastContext = this
        L.logEnable = true
        initCacheDir()
    }

    private fun initCacheDir() {
        CacheData.cachePath = getExternalFilesDir("")?.absolutePath
        RecordCache.audioCachePath = CacheData.cachePath + File.separator + "audio"
        L.vv("initCacheDir -> cachePath: ${CacheData.cachePath}")
        L.vv("initCacheDir -> audioCachePath: ${RecordCache.audioCachePath}")
    }

}