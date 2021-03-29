package com.junpu.aidl.service.demo

import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IUpdataInfo {

    private var receiver: MsgReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        info?.text = null

        receiver = MsgReceiver(this)
        val intentFilter = IntentFilter(MsgReceiver.ACTION_MSG_RECEIVER)
        registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let { unregisterReceiver(receiver) }
        receiver = null
    }

    override fun updateInfo(text: String?) {
        info?.appendln(text)
    }

}
