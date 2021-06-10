package com.junpu.customview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.junpu.customview.ui.MistakeHomeChartBean
import com.junpu.utils.format
import kotlin.math.ceil

private val COLOR_CHART = Color.parseColor("#4e92df")
private val COLOR_TEXT = Color.parseColor("#888888")
private val COLOR_LINE = Color.parseColor("#d2d2d2")
private const val PADDING_TOP = 40f
private const val PADDING_BOTTOM = 60f
private const val PADDING_LEFT = 60f
private const val ITEM_WIDTH = 60f // 柱状图柱子宽度
private const val TEXT_SPACING = 10f
private const val LINE_COUNT = 4 // y 轴需要几根刻度线（不算 x 轴线）
private const val TOP_LINE_MIN_COUNT = 4 // 顶部刻度线的最小值

/**
 * 错题本柱状图
 * @author junpu
 * @date 2021/6/2
 */
class MistakeChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 24f
    }

    // 图表柱状图区的宽高（不算 x、y 轴的文字区域）
    private var chartWidth = 0f
    private var chartHeight = 0f

    private var data: List<MistakeHomeChartBean>? = null
    private var fontMetrics = Paint.FontMetrics()
    private var bounds = Rect()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        chartWidth = width - PADDING_LEFT
        chartHeight = height - PADDING_TOP - PADDING_BOTTOM
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val data = data ?: return

        // 根据 count 的最大值，计算出顶部刻度线的值 maxY
        val maxCount = data.maxOfOrNull { it.count } ?: TOP_LINE_MIN_COUNT
        val maxY = ceil(maxCount.toFloat() / LINE_COUNT) * LINE_COUNT

        // 图表区域坐标
        val l = PADDING_LEFT
        val t = PADDING_TOP
        val r = l + chartWidth
        val b = t + chartHeight

        // 画Y轴刻度线
        paint.textAlign = Paint.Align.RIGHT
        paint.getTextBounds("0", 0, 1, bounds)
        for (i in 0..LINE_COUNT) {
            val lineY = b - chartHeight / LINE_COUNT * i

            // 画刻度线
            paint.color = COLOR_LINE
            canvas.drawLine(l, lineY, r, lineY, paint)

            // 画刻度文字
            paint.color = COLOR_TEXT
            val text = (maxY / LINE_COUNT * i).toInt().toString()
            val textX = l - TEXT_SPACING
            val textY = lineY - (bounds.top + bounds.bottom) / 2f
            canvas.drawText(text, textX, textY, paint)
        }

        // 画柱状图
        paint.textAlign = Paint.Align.CENTER
        paint.getFontMetrics(fontMetrics)
        val itemSpacing = (chartWidth - data.size * ITEM_WIDTH) / data.size
        data.forEachIndexed { i, item ->
            val itemHeight = (item.count / maxY) * chartHeight
            val il = PADDING_LEFT + itemSpacing / 2f + (ITEM_WIDTH + itemSpacing) * i
            val it = b - itemHeight
            val itemCenterX = il + ITEM_WIDTH / 2f

            // 画柱子
            paint.color = COLOR_CHART
            canvas.drawRect(il, it, il + ITEM_WIDTH, b, paint)

            // 画 count
            paint.color = COLOR_TEXT
            val countText = item.count.toString()
            val countY = it - TEXT_SPACING - fontMetrics.bottom
            canvas.drawText(countText, itemCenterX, countY, paint)

            // 画日期
//            val date = item.date.formatDateWithoutYearNotNull.toString()
            val date = item.date.format("M-d").toString()
            val dateY = b + TEXT_SPACING - fontMetrics.top
            canvas.drawText(date, itemCenterX, dateY, paint)
        }
    }

    fun setData(list: List<MistakeHomeChartBean>) {
        this.data = list
        invalidate()
    }
}
