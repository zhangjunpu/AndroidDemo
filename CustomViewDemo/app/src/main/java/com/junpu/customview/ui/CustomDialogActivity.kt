package com.junpu.customview.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.R
import com.junpu.customview.view.FloatingActionMenu
import com.junpu.toast.toast
import kotlinx.android.synthetic.main.activity_custom_dialog.*


/**
 *
 * @author junpu
 * @date 2019-07-14
 */
class CustomDialogActivity : AppCompatActivity() {

    //判断是否点击过
    private var state = false
    private val handler = Handler(Looper.getMainLooper())
    private var width: Int = 0
    private var height: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_dialog)
        init()
        one?.setOnClickListener {
            if (!state) {
                one?.isClickable = false
                two?.animate()?.xBy(-100f)?.yBy(-100f)?.setDuration(200L)?.start()
                three?.animate()?.xBy(100f)?.yBy(-100f)?.setDuration(200L)?.start()
                handler.postDelayed({
                    one?.isClickable = true
                    two?.isClickable = true
                    three?.isClickable = true
                    state = true
                }, 200)

            } else {
                one?.isClickable = false
                two?.isClickable = false
                three?.isClickable = false
                two?.animate()?.xBy(100f)?.yBy(100f)?.setDuration(200L)?.start()
                three?.animate()?.xBy(-100f)?.yBy(100f)?.setDuration(200L)?.start()
                handler.postDelayed({
                    one?.isClickable = true
                    state = false
                }, 200)
            }
        }

        btnAction?.setOnActionClickListener(object : FloatingActionMenu.OnActionClickListener {
            override fun onActionClick(view: View) {
                println("view.id = ${view.id}")
                when (view.id) {
                    R.id.btn2 -> toast("click btn2")
                    R.id.btn3 -> toast("click btn3")
                }
            }
        })
    }

    private fun init() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        width = metrics.widthPixels
        height = metrics.heightPixels
        println("$width/$height")
    }

}