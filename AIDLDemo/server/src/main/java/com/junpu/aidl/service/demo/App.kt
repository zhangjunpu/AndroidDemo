package com.junpu.aidl.service.demo

import android.app.Application
import com.junpu.toast.toastContext
import com.junpu.utils.app

/**
 * @author junpu
 * @date 2022/5/28
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
        toastContext = this
    }
}