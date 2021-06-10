package com.junpu.customview.view.daily

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

private val COLOR_BG = Color.parseColor("#F7F9FC")
private val COLOR_ACCENT = Color.parseColor("#307BF8")
private val COLOR_TEXT = Color.parseColor("#333333")
private const val RADIUS_RECT = 10f
private const val RADIUS_CIRCLE = 12f
private const val LINE_WIDTH = 2.5f

/**
 * 日报主要出错原因
 * @author junpu
 * @date 2021/6/1
 */
class DailyErrorCauseView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        strokeCap = Paint.Cap.ROUND
    }
    private var bounds = Rect()
    private var path = Path()

    private var number = ""
    private var text = ""

    /**
     * 设置数据
     */
    fun setData(num: Int, text: String) {
        this.number = String.format("%02d", num)
        this.text = text
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val cy = h / 2f
        val lineX = w * .2f

        // 画背景
        paint.style = Paint.Style.FILL
        paint.color = COLOR_BG
        canvas.drawRoundRect(0f, 0f, w, h, RADIUS_RECT, RADIUS_RECT, paint)

        // 画蓝色圆角矩形
        paint.color = COLOR_ACCENT
        canvas.drawRoundRect(0f, 0f, lineX, h, RADIUS_RECT, RADIUS_RECT, paint)

        // 画编号
        paint.textSize = 36f
        paint.color = Color.WHITE
        paint.getTextBounds(number, 0, number.length, bounds)
        val numX = lineX / 2f
        val numY = cy - (bounds.top + bounds.bottom) / 2f
        canvas.drawText(number, numX, numY, paint)

        // 画文字
        paint.textSize = 30f
        paint.color = COLOR_TEXT
        paint.getTextBounds(text, 0, text.length, bounds)
        val textX = (w + lineX) / 2f
        val textY = cy - (bounds.top + bounds.bottom) / 2f
        canvas.drawText(text, textX, textY, paint)

        // 画实心圆
        paint.color = COLOR_ACCENT
        canvas.drawCircle(lineX, cy, RADIUS_CIRCLE - LINE_WIDTH / 2f, paint)

        // 画空心圆
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        paint.strokeWidth = LINE_WIDTH
        canvas.drawCircle(lineX, cy, RADIUS_CIRCLE, paint)

        // 画箭头
        val aw = 3f
        val ah = 5f
        paint.strokeWidth = 2f
        path.run {
            moveTo(lineX - aw, cy - ah)
            lineTo(lineX + aw, cy)
            lineTo(lineX - aw, cy + ah)
        }
        canvas.drawPath(path, paint)
    }
}