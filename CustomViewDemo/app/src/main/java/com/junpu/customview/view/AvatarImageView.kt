package com.junpu.customview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.junpu.utils.dip

/**
 * 带边框的头像 ImageView
 * @author : Junpu
 * @data : 2022/7/20
 */
class AvatarImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    var strokeWidth = 2.dip.toFloat() // 边框宽度
    var strokeColor = Color.WHITE // 边框颜色

    private var cx = 0f
    private var cy = 0f
    private var radius = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = this@AvatarImageView.strokeWidth
        style = Paint.Style.STROKE
        color = strokeColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cx = (width shr 1).toFloat()
        cy = (height shr 1).toFloat()
        radius = cx - strokeWidth / 2 + 1
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(cx, cy, radius, paint)
    }
}