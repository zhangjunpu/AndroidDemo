package com.junpu.customview.view.daily

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import com.junpu.customview.ui.DailyKnowledgeInfo
import com.junpu.utils.dip

private val COLOR_BG = Color.parseColor("#EAEAEA")
private val COLOR_START = Color.parseColor("#54D1B6")
private val COLOR_END = Color.parseColor("#3481F2")
private val COLOR_TEXT = Color.parseColor("#474751")
private val ITEM_HEIGHT = dip(10).toFloat()
private val TEXT_SPACING = dip(5).toFloat()
private val MIN_HEIGHT = dip(35)

/**
 * 薄弱知识点
 * @author junpu
 * @date 2020/12/7
 */
class DailyWeakKnowledgeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = dip(14).toFloat()
    }
    private var fontMetrics = Paint.FontMetrics()
    private var linearGradient: LinearGradient? = null

    private var name: String? = null
    private var count = 0
    private var maxCount = 0

    private var progressWidth = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        // 宽是 WRAP_CONTENT 按照 MATCH_PARENT 算
        if (widthMode == MeasureSpec.AT_MOST)
            widthSize -= paddingStart + paddingEnd + marginStart + marginEnd

        // 高是 WRAP_CONTENT 则设定最小高度
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST)
            setMeasuredDimension(widthSize, MIN_HEIGHT)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initShader()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = width.toFloat()
        val b = height.toFloat()

        // 画进度条背景
        val pt = b - ITEM_HEIGHT
        val radius = ITEM_HEIGHT / 2f
        paint.color = COLOR_BG
        canvas.drawRoundRect(0f, pt, r, b, radius, radius, paint)

        // 画进度条
        paint.color = COLOR_START
        paint.shader = linearGradient
        canvas.drawRoundRect(0f, pt, progressWidth, b, radius, radius, paint)

        // 画名称
        paint.color = COLOR_TEXT
        paint.shader = null
        paint.getFontMetrics(fontMetrics)
        val textY = pt - TEXT_SPACING - fontMetrics.bottom
        name?.let {
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(it, 0f, textY, paint)
        }

        // 画count
        paint.textAlign = Paint.Align.RIGHT
        val text = "${count}题"
        canvas.drawText(text, r - TEXT_SPACING, textY, paint)
    }

    /**
     * 设置数据
     */
    fun setData(data: DailyKnowledgeInfo, max: Int) {
        name = data.knowledgeName
        count = data.knowledgeCount
        maxCount = max
        if (width > 0) initShader()
        invalidate()
    }

    /**
     * 初始化着色器
     */
    private fun initShader() {
        val oldWidth = progressWidth
        progressWidth = (count / maxCount.toFloat()) * width
        if (progressWidth != oldWidth) {
            linearGradient = LinearGradient(
                0f,
                0f,
                progressWidth,
                0f,
                COLOR_START,
                COLOR_END,
                Shader.TileMode.CLAMP
            )
        }
    }
}