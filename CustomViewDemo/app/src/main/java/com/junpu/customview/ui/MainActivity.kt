package com.junpu.customview.ui

import android.Manifest
import android.os.Bundle
import com.junpu.customview.databinding.ActivityMainBinding
import com.junpu.gopermissions.PermissionsActivity
import com.junpu.utils.launch
import com.junpu.viewbinding.binding

class MainActivity : PermissionsActivity() {

    private val binding by binding<ActivityMainBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            btnCustomView.setOnClickListener {
                launch<CustomViewActivity>()
            }
            btnCustomDialog.setOnClickListener {
                launch<CustomDialogActivity>()
            }
            btnCorrectMark.setOnClickListener {
                launch<CorrectMarkActivity>()
            }
            btnHtml.setOnClickListener {
                launch<HtmlActivity>()
            }
            btnCanvasDemo.setOnClickListener {
                launch<CanvasDemoActivity>()
            }
            btnAnimatorDemo.setOnClickListener {
                launch<AnimatorActivity>()
            }
            btnDashboard.setOnClickListener {
                launch<DashboardActivity>()
            }
            btnTextDemo.setOnClickListener {
                launch<TextDemoActivity>()
            }
            btnDaily.setOnClickListener {
                launch<DailyActivity>()
            }
            btnAvatar.setOnClickListener {
                launch<AvatarActivity>()
            }
            btnAvatar.setOnClickListener {
                launch<MistakeActivity>()
            }
            btnAvatar.setOnClickListener {
                launch<SaveInstanceStateActivity>()
            }
            btnAvatar.setOnClickListener {
                launch<CameraActivity>()
            }
            btnAvatarAnim.setOnClickListener {
                launch<AvatarAnimActivity>()
            }
            btnAvatarAnim.performClick()
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        )
    }
}
