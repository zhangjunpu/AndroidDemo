package com.junpu.aidl.service.demo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.junpu.aidl.service.demo.IMyAidlInterface

/**
 * AIDL服务
 * @author junpu
 * @date 2019/3/9
 */
class AidlService : Service() {

    private val intent by lazy { Intent(MsgReceiver.ACTION_MSG_RECEIVER) }

    private val binder = object : IMyAidlInterface.Stub() {
        override fun add(x: Int, y: Int): Int {
            updateInfo("服务端收到请求 加法：$x + $y")
            return x + y
        }

        override fun subtract(x: Int, y: Int): Int {
            updateInfo("服务端收到请求 减法：$x - $y")
            return x - y
        }

        override fun multiply(x: Int, y: Int): Int {
            updateInfo("服务端收到请求 乘法：$x * $y")
            return x * y
        }

        override fun divide(x: Int, y: Int): Int {
            updateInfo("服务端收到请求 除法：$x / $y")
            return x / y
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        updateInfo("AidlService.onBind")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        updateInfo("AidlService.onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateInfo("AidlService.onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateInfo("AidlService.onDestroy")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        updateInfo("AidlService.onUnbind")
        return super.onUnbind(intent)
    }

    /**
     * 更新首页信息
     */
    private fun updateInfo(text: String) {
        intent.putExtra(MsgReceiver.KEY_MSG, text)
        sendBroadcast(intent)
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}