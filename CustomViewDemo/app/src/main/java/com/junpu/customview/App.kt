package com.junpu.customview

import android.app.Application
import com.junpu.log.L
import com.junpu.toast.toastContext
import com.junpu.utils.app

/**
 *
 * @author junpu
 * @date 2019-10-17
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        toastContext = this
        L.logEnable = true
    }
}