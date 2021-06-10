package com.junpu.customview.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withClip
import com.junpu.customview.R

/**
 *
 * @author junpu
 * @date 2021/4/27
 */
class CanvasDemoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bitmap by lazy { BitmapFactory.decodeResource(resources, R.mipmap.test) }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 2f
        textSize = 40f
//        strokeJoin = Paint.Join.ROUND
//        strokeCap = Paint.Cap.ROUND
//        pathEffect = CornerPathEffect(120f)
    }
    private val path = Path().apply {
        moveTo(100f, 100f)
        lineTo(300f, 400f)
        lineTo(700f, 100f)
        lineTo(800f, 550f)
        lineTo(1000f, 250f)
        lineTo(1100f, 750f)
    }
    private val clipPath = Path().apply {
        addCircle(600f, 600f, 200f, Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        drawTextOnPath(canvas)
//        drawRunAdvance(canvas)
        clip(canvas)
    }

    private fun clip(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawBitmap(bitmap, null, Rect(0, 0, 1002, 818), null)
        canvas.restore()

        canvas.withClip(RectF(100f, 200f, 300f, 600f)) {
            canvas.drawBitmap(bitmap, null, Rect(0, 0, 1002, 818), null)
        }
    }

    private fun drawRunAdvance(canvas: Canvas) {
        val text = "All text outside the range contextStart..contextEnd is ignored."
        val x = 50f
        val y = 800f
        val length = text.length

        // 画光标
        canvas.drawText(text, x, y, paint)
        val advance = paint.getRunAdvance(text, 0, length, 0, length, false, 15)

        // 画光标
        val fontMetrics = paint.fontMetrics
        canvas.drawLine(
            x + advance,
            y + fontMetrics.ascent,
            x + advance,
            y + fontMetrics.descent,
            paint
        )

        // 显示边框
        val rect = Rect()
        paint.getTextBounds(text, 0, length, rect)
        rect.offset(x.toInt(), y.toInt())
        paint.style = Paint.Style.STROKE
        canvas.drawRect(rect, paint)
    }

    private fun drawTextOnPath(canvas: Canvas) {
//        val saveCount = canvas.saveLayer(0f, 0f, 1200f, 1800f, paint)
        paint.style = Paint.Style.STROKE
        canvas.drawPath(path, paint)
        paint.style = Paint.Style.FILL
        val text =
            "All text outside the range contextStart..contextEnd is ignored. The text between start and end will be laid out to be measured."
        canvas.drawTextOnPath(text, path, 0f, 0f, paint)
//        canvas.restoreToCount(saveCount)
    }

}