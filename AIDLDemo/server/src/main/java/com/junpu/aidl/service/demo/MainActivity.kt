package com.junpu.aidl.service.demo

import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.junpu.aidl.service.demo.databinding.ActivityMainBinding
import com.junpu.utils.appendLine
import com.junpu.viewbinding.binding

class MainActivity : AppCompatActivity(), IUpdataInfo {

    private val binding by binding<ActivityMainBinding>()
    private var receiver: MsgReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.textInfo.text = null

        receiver = MsgReceiver(this)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver!!, IntentFilter(MsgReceiver.ACTION_MSG_RECEIVER))
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let { LocalBroadcastManager.getInstance(this).unregisterReceiver(it) }
        receiver = null
    }

    override fun updateInfo(text: String?) {
        binding.textInfo.appendLine(text)
    }
}
