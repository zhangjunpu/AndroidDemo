package com.junpu.aidl.client.demo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.junpu.aidl.service.demo.IMyAidlInterface
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var aidlInterface: IMyAidlInterface? = null
    private val x = 10
    private val y = 3

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            println("Client MainActivity.onServiceDisconnected")
            info?.appendln("远程服务已断开")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            println("Client MainActivity.onServiceConnected")
            aidlInterface = IMyAidlInterface.Stub.asInterface(service)
            info?.appendln("远程服务已连接")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        info?.text = null
        btnBind?.setOnClickListener { bindService() }
        btnAdd?.setOnClickListener { mathAdd() }
        btnSubtract?.setOnClickListener { mathSubtract() }
        btnMultiply?.setOnClickListener { mathMultiply() }
        btnDivide?.setOnClickListener { mathDivide() }
    }

    private fun bindService() {
        info?.appendln("开始绑定远程服务：com.junpu.aidl.service.demo.IMyAidlInterface")
        val intent = Intent("com.junpu.aidl.service.demo.IMyAidlInterface")
        intent.setPackage("com.junpu.aidl.service.demo")
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    /**
     * 请求加法
     */
    private fun mathAdd() {
        try {
            info?.appendln("请求远程服务方法：add")
            val result = aidlInterface?.add(x, y)
            println("$x + $y = $result")
            info?.appendln("远程方法返回结果：$x + $y = $result")
        } catch (e: RemoteException) {
            Log.e("system.err", Log.getStackTraceString(e))
        }
    }

    /**
     * 请求减法
     */
    private fun mathSubtract() {
        try {
            info?.appendln("请求远程服务方法：subtract")
            val result = aidlInterface?.subtract(x, y)
            println("$x - $y = $result")
            info?.appendln("远程方法返回结果：$x - $y = $result")
        } catch (e: RemoteException) {
            Log.e("system.err", Log.getStackTraceString(e))
        }
    }

    /**
     * 请求乘法
     */
    private fun mathMultiply() {
        try {
            info?.appendln("请求远程服务方法：multiply")
            val result = aidlInterface?.multiply(x, y)
            println("$x * $y = $result")
            info?.appendln("远程方法返回结果：$x * $y = $result")
        } catch (e: RemoteException) {
            Log.e("system.err", Log.getStackTraceString(e))
        }
    }

    /**
     * 请求除法
     */
    private fun mathDivide() {
        try {
            info?.appendln("请求远程服务方法：divide")
            val result = aidlInterface?.divide(x, y)
            println("$x / $y = $result")
            info?.appendln("远程方法返回结果：$x / $y = $result")
        } catch (e: RemoteException) {
            Log.e("system.err", Log.getStackTraceString(e))
        }
    }

}
