package com.junpu.customview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.databinding.ActivityDanmuAnimBinding
import com.junpu.viewbinding.binding

/**
 * 弹幕
 * @author Junpu
 * @date 2022/9/15
 */
class DanmuAnimActivity : AppCompatActivity() {
    private val binding by binding<ActivityDanmuAnimBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = mutableListOf(
            "富龙滑雪场",
            "怀北国际滑雪场",
            "北大壶滑雪场",
            "万龙滑雪场",
            "莲花山滑雪场",
            "云顶滑雪场",
            "南山滑雪场",
            "张家口滑雪场",
        )

        binding.run {
            view.setData(data)
            btnStart.setOnClickListener { view.startAnim() }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.view.startAnim()
    }

    override fun onPause() {
        super.onPause()
        binding.view.stopAnim()
    }
}
