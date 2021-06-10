package com.junpu.customview.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.R
import com.junpu.log.L
import kotlinx.android.synthetic.main.activity_animator.*
import java.io.File

/**
 *
 * @author junpu
 * @date 2021/5/7
 */
class AnimatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animator)
        btnStart.setOnClickListener {
            animatorView.setProgressByAnimator(60f)
        }
        test()
    }

    private fun test() {
        val dexDir = getDir("dex", Context.MODE_PRIVATE)
        L.vv("dexDir: $dexDir")

        val path = getExternalFilesDir("pdf")
        val file = File(path, "test.pdf")
        file.createNewFile()
        L.vv(file)
    }
}

/**
 * Animator
 * @author junpu
 * @date 2021/5/7
 */
class AnimatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 30f * Resources.getSystem().displayMetrics.scaledDensity
        color = Color.RED
    }
    private val rect = RectF()
    private val bounds = Rect()

    var radius = 150f
    var maxProgress: Float = 100f
    var progress: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val cx = width / 2f
        val cy = height / 2f
        rect.set(cx - radius, cy - radius, cx + radius, cy + radius)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#00ffff"))
        val percent = progress / maxProgress
        canvas.drawArc(rect, 0f, percent * 360f, false, paint)

        val text = "${(percent * 100).toInt()}%"
        textPaint.getTextBounds(text, 0, text.length, bounds)
        val x = (width - bounds.width()) / 2f
        val y = (height - bounds.top - bounds.bottom) / 2f
        canvas.drawText(text, x, y, textPaint)
    }

    fun setProgressByAnimator(value: Float) {
        ObjectAnimator.ofFloat(this, "progress", value)
            .start()
    }
}