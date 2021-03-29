package com.junpu.customview.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.junpu.customview.R
import com.junpu.customview.bean.CorrectMarkBean
import com.junpu.log.logStackTrace
import com.junpu.utils.readAssetsFile
import kotlinx.android.synthetic.main.activity_correct_mark.*

/**
 *
 * @author junpu
 * @date 2019-10-16
 */
class CorrectMarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correct_mark)
        initMark()
    }

    private fun initMark() {
        val json = readAssetsFile("correct_mark_location.json")

        try {
            val options = BitmapFactory.Options().apply { inScaled = false }
            val bitmap = BitmapFactory.decodeResource(resources, R.raw.img, options)
            val correctMarkBean = Gson().fromJson(json, CorrectMarkBean::class.java)
            markView?.setMarkBg(bitmap)
            markView?.setMarkData(correctMarkBean)
            markView?.postInvalidate()
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }
}