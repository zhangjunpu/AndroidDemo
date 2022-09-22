package com.junpu.customview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.databinding.ActivityAvatarAnimBinding
import com.junpu.utils.postDelay
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
            "https://c-ssl.duitang.com/uploads/blog/202209/21/20220921101700_9769e.png",
            "https://c-ssl.duitang.com/uploads/item/201908/19/20190819150344_ALnaX.jpeg",
            "https://c-ssl.duitang.com/uploads/item/202005/02/20200502185802_FuFU2.jpeg",
            "https://c-ssl.duitang.com/uploads/blog/202209/21/20220921101700_32f41.jpeg",
            "https://c-ssl.duitang.com/uploads/blog/202209/21/20220921101700_23ef0.jpeg"
        )

        binding.run {
            btnNextFrame.setOnClickListener {
                view.nextFrame()
            }
        }

        postDelay(0) { binding.view.setData(data) }
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
