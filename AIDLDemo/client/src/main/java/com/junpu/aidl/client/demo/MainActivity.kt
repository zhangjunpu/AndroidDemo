package com.junpu.aidl.client.demo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.junpu.aidl.client.demo.databinding.ActivityMainBinding
import com.junpu.aidl.service.IMyAidlInterface
import com.junpu.utils.appendLine
import com.junpu.viewbinding.binding

class MainActivity : AppCompatActivity() {

    private val binding by binding<ActivityMainBinding>()
    private var aidlInterface: IMyAidlInterface? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aidlInterface = IMyAidlInterface.Stub.asInterface(service)
            binding.textInfo.appendLine("远程服务已连接")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binding.textInfo.appendLine("远程服务已断开")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val x = 10
        val y = 3
        binding.run {
            textInfo.text = null
            btnBind.setOnClickListener {
                binding.textInfo.appendLine("开始绑定远程服务：com.junpu.aidl.service.IMyAidlInterface")
                val intent = Intent("com.junpu.aidl.service.IMyAidlInterface").apply {
                    setPackage("com.junpu.aidl.service.demo")
                }
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
            btnAdd.setOnClickListener {
                // 请求加法
                try {
                    binding.textInfo.appendLine("请求远程服务方法：add")
                    val result = aidlInterface?.add(x, y)
                    binding.textInfo.appendLine("远程方法返回结果：$x + $y = $result")
                } catch (e: RemoteException) {
                    Log.e("system.err", Log.getStackTraceString(e))
                }
            }
            btnSubtract.setOnClickListener {
                // 请求减法
                try {
                    binding.textInfo.appendLine("请求远程服务方法：subtract")
                    val result = aidlInterface?.sub(x, y)
                    binding.textInfo.appendLine("远程方法返回结果：$x - $y = $result")
                } catch (e: RemoteException) {
                    Log.e("system.err", Log.getStackTraceString(e))
                }
            }
            btnMultiply.setOnClickListener {
                // 请求乘法
                try {
                    binding.textInfo.appendLine("请求远程服务方法：multiply")
                    val result = aidlInterface?.mul(x, y)
                    binding.textInfo.appendLine("远程方法返回结果：$x * $y = $result")
                } catch (e: RemoteException) {
                    Log.e("system.err", Log.getStackTraceString(e))
                }
            }
            btnDivide.setOnClickListener {
                // 请求除法
                try {
                    binding.textInfo.appendLine("请求远程服务方法：divide")
                    val result = aidlInterface?.div(x, y)
                    binding.textInfo.appendLine("远程方法返回结果：$x / $y = $result")
                } catch (e: RemoteException) {
                    Log.e("system.err", Log.getStackTraceString(e))
                }
            }
        }
    }

}
