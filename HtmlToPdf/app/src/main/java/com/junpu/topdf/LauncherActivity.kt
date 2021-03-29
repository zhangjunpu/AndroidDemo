package com.junpu.topdf

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.junpu.gopermissions.PermissionsActivity

/**
 *
 * @author junpu
 * @date 2020/7/21
 */
class LauncherActivity : PermissionsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}