package com.junpu.customview.view.daily

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.junpu.customview.R
import com.junpu.customview.ui.DailyChart
import com.junpu.utils.format

private const val PADDING_TOP = 60f // 图表顶部空白
private const val PADDING_BOTTOM = 140f // 图表底部空白
private const val CIRCLE_RADIUS = 12f // 折线图小圆半径
private const val ITEM_WIDTH = 68f // 柱状图柱子宽度
private const val TEXT_SPACING = 10f
private const val LINE_WIDTH = 4f
private val COLOR_COLUMN = Color.parseColor("#357EF8")
private val COLOR_LINE = Color.parseColor("#FFCD7A")
private val COLOR_TEXT = Color.parseColor("#999999")

/**
 * 日报图表
 * @author junpu
 * @date 2021/6/1
 */
class DailyChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = LINE_WIDTH
    }

    // 图表区尺寸
    private var chartWidth = 0f
    private var chartHeight = 0f
    private var columnChartHeight = 0f // 柱状图高度
    private var lineChartHeight = 0f // 折线图高度

    private var data: List<DailyChart>? = null
    private var fontMetrics = Paint.FontMetrics()
    private var bounds = Rect()

    private val lineIcon by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_line_chart_icon)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        chartWidth = width.toFloat()
        chartHeight = height - PADDING_TOP - PADDING_BOTTOM
        columnChartHeight = chartHeight * .6f
        lineChartHeight = chartHeight * .5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val data = data ?: return
        val itemSpacing = (chartWidth - data.size * ITEM_WIDTH) / data.size
        val maxCount = data.maxOfOrNull { it.count } ?: 0
        val bottom = height - PADDING_BOTTOM

        paint.color = COLOR_COLUMN
        paint.textSize = 30f
        paint.textAlign = Paint.Align.CENTER
        paint.getFontMetrics(fontMetrics)
        val fontOffset = fontMetrics.bottom

        var lastX = 0f
        var lastY = 0f
        var hasPoint = false
        data.forEachIndexed { index, item ->
            var columnItemHeight = (item.count / maxCount.toFloat()) * columnChartHeight
            // count 为0添加默认2像素高度
            if (columnItemHeight == 0f || columnItemHeight.isNaN()) columnItemHeight = 2f
            val l = itemSpacing / 2f + index * (ITEM_WIDTH + itemSpacing)
            val t = bottom - columnItemHeight
            val r = l + ITEM_WIDTH
            val itemCenterX = l + ITEM_WIDTH / 2f

            // 画柱状图
            paint.style = Paint.Style.FILL
            paint.color = COLOR_COLUMN
            canvas.drawRect(l, t, r, bottom, paint)

            // 画日期
            paint.color = COLOR_TEXT
            paint.textSize = 30f
            val text = item.date.format("M.dd").toString()
            val dateY = bottom + 40f + TEXT_SPACING - fontOffset
            canvas.drawText(text, itemCenterX, dateY, paint)

            // 画数量
            val count = item.count.toString()
            val countY = t - TEXT_SPACING - fontOffset
            canvas.drawText(count, itemCenterX, countY, paint)

            // 画折线图
            paint.color = COLOR_LINE
            paint.textSize = 28f
            item.percent?.let {
                val lineY = ((100 - it) / 100f) * lineChartHeight + PADDING_TOP
                item.px = itemCenterX
                item.py = lineY

                // 画线
                if (hasPoint) canvas.drawLine(lastX, lastY, itemCenterX, lineY, paint)

                // 画百分比
                val percent = item.percent.toString()
                val percentY = lineY - CIRCLE_RADIUS - LINE_WIDTH / 2f - TEXT_SPACING - fontOffset
                canvas.drawText(percent, itemCenterX, percentY, paint)

                lastX = itemCenterX
                lastY = lineY
            }
            hasPoint = item.percent != null
        }

        // 画圆
        data.forEach {
            it.percent?.run {
                // 画空心圆
                paint.style = Paint.Style.STROKE
                paint.color = COLOR_LINE
                canvas.drawCircle(it.px, it.py, CIRCLE_RADIUS, paint)

                // 画实心圆
                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                canvas.drawCircle(it.px, it.py, CIRCLE_RADIUS - LINE_WIDTH / 2, paint)
            }
        }

        // 画错题数量图例
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 26f
        paint.color = COLOR_COLUMN
        val l = width / 6f
        val r = l + 44f
        val b = height - 10f
        val t = b - 30f
        canvas.drawRect(l, t, r, b, paint)
        paint.color = COLOR_TEXT
        val text = "错题数量"
        paint.getTextBounds(text, 0, text.length, bounds)
        val textY = t + 15f - (bounds.top + bounds.bottom) / 2f
        canvas.drawText(text, r + 20f, textY, paint)

        // 画正确率图例
        val icon = lineIcon ?: return
        val ll = width * 4f / 7f
        val lt = t + 5f
        val lr = ll + 72f
        val lb = lt + 20f
        icon.setBounds(ll.toInt(), lt.toInt(), lr.toInt(), lb.toInt())
        icon.draw(canvas)
        canvas.drawText("题目正确率/%", lr + 20f, textY, paint)
    }

    fun setData(list: List<DailyChart>) {
        this.data = list
        invalidate()
    }
}
