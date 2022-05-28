package com.junpu.aidl.service.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 *
 * @author junpu
 * @date 2019/3/9
 */
class MsgReceiver(private val updateInfo: IUpdataInfo) : BroadcastReceiver() {

    companion object {
        const val ACTION_MSG_RECEIVER = "com.junpu.aidl.service.MSG_RECEIVER"
        const val KEY_MSG = "msg"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_MSG_RECEIVER -> {
                val text = intent.getStringExtra(KEY_MSG)
                updateInfo.updateInfo(text)
            }
        }
    }
}