package com.junpu.customview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.databinding.ActivityAvatarAnimBinding
import com.junpu.viewbinding.binding

/**
 * 头像组动画
 * @author : Junpu
 * @data : 2022/7/20
 */
class AvatarAnimActivity : AppCompatActivity() {
    private val binding by binding<ActivityAvatarAnimBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = mutableListOf(
            "https://picsum.photos/100",
            "https://picsum.photos/150",
            "https://picsum.photos/200",
            "https://picsum.photos/250",
            "https://picsum.photos/300"
        )

        binding.run {
            view.setData(data)
            btnNextFrame.setOnClickListener {
                view.nextFrame()
            }
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
