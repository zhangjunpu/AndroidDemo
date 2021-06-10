package com.junpu.customview.ui

import android.Manifest
import android.os.Bundle
import com.junpu.customview.R
import com.junpu.gopermissions.PermissionsActivity
import com.junpu.utils.launch
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : PermissionsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCustomView?.setOnClickListener {
            launch<CustomViewActivity>()
        }
        btnCustomDialog?.setOnClickListener {
            launch<CustomDialogActivity>()
        }
        btnCorrectMark?.setOnClickListener {
            launch<CorrectMarkActivity>()
        }
        btnHtml?.setOnClickListener {
            launch<HtmlActivity>()
        }
        btnCanvasDemo?.setOnClickListener {
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
