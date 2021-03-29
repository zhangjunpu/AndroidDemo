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
            launch(CustomViewActivity::class.java)
        }
        btnCustomDialog?.setOnClickListener {
            launch(CustomDialogActivity::class.java)
        }
        btnCorrectMark?.setOnClickListener {
            launch(CorrectMarkActivity::class.java)
        }
        btnHtml?.setOnClickListener {
            launch(HtmlActivity::class.java)
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
