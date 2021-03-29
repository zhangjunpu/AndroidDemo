package com.junpu.customview.ui

import android.animation.IntEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import androidx.core.os.bundleOf
import com.junpu.customview.R
import com.junpu.utils.launch
import kotlinx.android.synthetic.main.activity_custom_view.*

class CustomViewActivity : AppCompatActivity() {

    private var doFlag = true
    private var p = 0
    private var curProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)

        bundleOf()

        circleProgressBar?.setProgressByAnimation(40)
        btnTest?.setOnClickListener {
            circleProgressBar?.setProgressByAnimation(30)
            performViewWrapper(it)
        }
        textTest?.setOnClickListener {
            println("it.top = ${it.top}, it.bottom = ${it.bottom}")
            preformAnimator(it, it.height, 1710 - it.top)
        }
        textProperty?.setOnClickListener {
            circleProgressBar?.setProgressByAnimation(90)
            println("MainActivity.onCreate: rotation = ${it.rotation}")
            val rotation = it.rotation
            val targetRotation = if (rotation >= 3600f) rotation - 3600f else rotation + 3600f
            it.animate()
                .rotation(targetRotation)
                .setDuration(3000L)
                .start()
        }
        btnCustomDialog?.setOnClickListener { launch(CustomDialogActivity::class.java) }
        btnProgress?.setOnClickListener {
            val progress = curProgress.toFloat() / 100
            println("progress = $progress")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar?.setProgress(curProgress, true)
            } else {
                progressBar?.progress = curProgress
            }
            linearProgressBar?.setProgressByAnimator(curProgress)
            gradientProgressBar?.setProgressByAnimator(curProgress)
            curProgress += 10
            if (curProgress > 100) curProgress = 0
        }
    }

    override fun onResume() {
        super.onResume()
        doFlag = true
//        startProgress()
    }

    override fun onPause() {
        super.onPause()
        doFlag = false
    }

    private fun startProgress() {
        Thread(Runnable {
            while (doFlag && p <= 100) {
                HandlerCompat.postDelayed(Handler(Looper.getMainLooper()), {
                    println("p = $p")
                    circleProgressBar.progress = p
                }, null, 10)
//                handler.sendEmptyMessage(1)
                Thread.sleep(100)
                p++
            }
        }).start()
    }

    /**
     * 两种方式改变View宽度
     * 方式一：通过包装类包装View提供get、set方法
     */
    private fun performViewWrapper(target: View) {
        class ViewWrapper(val view: View) {
            fun getWidth(): Int = view.width
            fun setWidth(width: Int) {
                view.layoutParams.width = width
                view.requestLayout()
            }
        }

        val view = ViewWrapper(target)
        ObjectAnimator.ofInt(view, "width", 1080)
            .setDuration(3000L)
            .start()
    }

    /**
     * 两种方式改变View宽度
     * 方式二：通过ValueAnimator监听动画实现，自己改变View属性
     */
    private fun preformAnimator(target: View, start: Int, end: Int) {
        val animator = ValueAnimator.ofInt(0, 100)
        val evaluator = IntEvaluator()
        animator.addUpdateListener {
            target.layoutParams.height = evaluator.evaluate(it.animatedFraction, start, end)
            target.requestLayout()
            println("target.bottom = ${target.bottom}")
        }
        animator.duration = 3000L
        animator.start()
    }

}
