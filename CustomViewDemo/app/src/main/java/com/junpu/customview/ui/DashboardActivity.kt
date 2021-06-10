package com.junpu.customview.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.R
import com.junpu.utils.dip
import com.junpu.utils.sp
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 仪表盘
 * @author junpu
 * @date 2021/5/18
 */
class DashboardActivity : AppCompatActivity() {

    private var animator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        btnStart.setOnClickListener {
            if (animator == null) {
                animator = ObjectAnimator.ofInt(dashboardView, "speed", 0, 100).apply {
                    duration = 4400
                    interpolator = AccelerateInterpolator(1.5f)
                }
            }
            animator?.start()
        }
    }
}

val RADIUS = dip(100).toFloat()
const val START_ANGLE = 145f
const val SWEEP_ANGLE = 260f
val OFFSET = dip(10)
val RADIUS_POINT = dip(3).toFloat()

class DashboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dip(2).toFloat()
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = sp(8).toFloat()
    }

    private var rect = RectF()
    private var list = mutableListOf<Rulers>()
    private var cx: Float = 0f
    private var cy: Float = 0f

    private var maxSpeed = 220
    var speed = 0 // 当前速度
        set(value) {
            field = value
            invalidate()
        }

    private val tempPoint = PointF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cx = width / 2f
        cy = height / 2f
        rect.set(cx - RADIUS, cy - RADIUS, cx + RADIUS, cy + RADIUS)

        val speedStep = maxSpeed / 10
        val angleStep = SWEEP_ANGLE / speedStep
        for (i in 0..speedStep) {
            val angle = START_ANGLE + i * angleStep
            getPointByAngle(angle, tempPoint, RADIUS)
            val sx = tempPoint.x
            val sy = tempPoint.y
            getPointByAngle(angle, tempPoint, RADIUS - OFFSET)
            val ex = tempPoint.x
            val ey = tempPoint.y
            list.add(Rulers(angle, sx, sy, ex, ey, (i * 10).toString()))
        }
    }

    private fun getPointByAngle(
        angle: Float,
        tempPointF: PointF,
        r: Float = RADIUS,
        x: Float = cx,
        y: Float = cy
    ) {
        val deg = Math.toRadians(angle.toDouble())
        val sin = sin(deg)
        val cos = cos(deg)
        val sx = cos * r + x
        val sy = sin * r + y
        tempPointF.set(sx.toFloat(), sy.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画中心原点
        paint.style = Paint.Style.FILL
        canvas.drawCircle(cx, cy, RADIUS_POINT, paint)
        // 画弧
        paint.style = Paint.Style.STROKE
        canvas.drawArc(rect, START_ANGLE, SWEEP_ANGLE, false, paint)
        // 画刻度
        list.forEach {
            canvas.drawLine(it.sx, it.sy, it.ex, it.ey, paint)
            it.text?.let { text ->
                val w = textPaint.measureText(text)
                val h = textPaint.fontSpacing

                val angle = it.angle.toInt() % 360
                var angleX = angle
                if (angleX in 181..359) {
                    angleX -= 180
                }
                val offsetX = angleX / 180f * w - w

                var angleY = angle
                if (angleY in 0..89) {
                    angleY += 180
                } else if (angleY in 271..359) {
                    angleY -= 180
                }
                val offsetY = (angleY - 90) / 180f * h

                canvas.drawText(text, it.ex + offsetX, it.ey + offsetY, textPaint)
            }

        }
        // 画指针
        getPointByAngle(START_ANGLE + SWEEP_ANGLE * speed / maxSpeed, tempPoint)
        canvas.drawLine(cx, cy, tempPoint.x, tempPoint.y, paint)
    }
}

/**
 * 刻度标尺
 */
data class Rulers(
    var angle: Float,
    var sx: Float,
    var sy: Float,
    var ex: Float,
    var ey: Float,
    var text: String?,
)