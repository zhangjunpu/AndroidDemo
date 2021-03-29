package com.junpu.customview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.junpu.customview.R
import com.junpu.customview.bean.CorrectMarkBean
import com.junpu.utils.dip
import com.junpu.utils.sp

/**
 * Correct Mark View
 * @author junpu
 * @date 2019-10-16
 */
class CorrectMarkView : View {

    companion object {
        // 标记类型
        private const val MARK_TYPE_SYMBOL = "symbol"
        private const val MARK_TYPE_TEXT = "text"
        private const val MARK_TYPE_DRAWING = "drawing"

        // 符号类型
        private const val SYMBOL_TYPE_RIGHT = "correct"
        private const val SYMBOL_TYPE_WRONG = "wrong"

        // 默认值
        private const val DEFAULT_MARK_COLOR = "#2877FF"
        private const val DEFAULT_TEXT_SIZE = 10 // 默认文字大小 15sp
        private const val DEFAULT_SYMBOL_RIGHT_WIDTH = 24 // 默认对勾宽度 48dp
        private const val DEFAULT_SYMBOL_RIGHT_HEIGHT = 16 // 默认对勾高度 31dp
        private const val DEFAULT_SYMBOL_WRONG_WIDTH = 11 // 默认错叉宽度 22dp
        private const val DEFAULT_SYMBOL_WRONG_HEIGHT = 10 // 默认错叉高度 20dp
    }

    private var pathPaint: Paint? = null // drawing画笔
    private var textPaint: TextPaint? = null // 文字

    private var markColor = Color.parseColor(DEFAULT_MARK_COLOR)
    private var textSp = sp(DEFAULT_TEXT_SIZE).toFloat() // 文字尺寸
    private val symbolRightWidth by lazy { dip(DEFAULT_SYMBOL_RIGHT_WIDTH) } // 对勾宽度
    private val symbolRightHeight by lazy { dip(DEFAULT_SYMBOL_RIGHT_HEIGHT) } // 对勾高度
    private val symbolWrongWidth by lazy { dip(DEFAULT_SYMBOL_WRONG_WIDTH) } // 错叉宽度
    private val symbolWrongHeight by lazy { dip(DEFAULT_SYMBOL_WRONG_HEIGHT) } // 错叉高度
    private val symbolRight by lazy { resources.getDrawable(R.drawable.ic_mark_symbol_right) } // 对勾符号
    private val symbolWrong by lazy { resources.getDrawable(R.drawable.ic_mark_symbol_wrong) } // 错叉符号

    private var markData: CorrectMarkBean? = null // 标记bean
    private var markBg: Bitmap? = null // mark背景

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        // 文字Paint
        textPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = textSp
            textAlign = Paint.Align.LEFT
            color = markColor
        }
        // path Paint
        pathPaint = Paint().apply {
            isAntiAlias = true
            color = markColor
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    fun setMarkBg(bitmap: Bitmap) {
        markBg = bitmap
    }

    /**
     * 设置标记数据
     */
    fun setMarkData(markData: CorrectMarkBean?) {
        this.markData = markData
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 画背景
        markBg?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        // 画符号
        markData?.list?.filter { it.type == MARK_TYPE_SYMBOL }?.forEach {
            when (it.symbol) {
                SYMBOL_TYPE_RIGHT -> {
                    val scale = it.scale
                    val width = symbolRightWidth * scale
                    val height = symbolRightHeight * scale
                    val left = (it.x - symbolRightWidth / 2).toInt() // 注意
                    val top = (it.y - symbolRightHeight).toInt() // 注意
                    val right = (left + width).toInt()
                    val bottom = (top + height).toInt()
                    symbolRight?.apply {
                        setBounds(left, top, right, bottom)
                    }
                }
                SYMBOL_TYPE_WRONG -> {
                    val scale = it.scale
                    val width = symbolWrongWidth * scale
                    val height = symbolWrongHeight * scale

                    val left = (it.x - width / 2).toInt()
                    val top = (it.y - height / 2).toInt()
                    val right = (left + width).toInt()
                    val bottom = (top + height).toInt()
                    symbolWrong?.apply {
                        setBounds(left, top, right, bottom)
                    }
                }
                else -> null
            }?.run {
                draw(canvas)
            }
        }

        // 画文字
        markData?.list?.filter { it.type == MARK_TYPE_TEXT }?.forEach {
            val textSize = textSp * it.scale
            textPaint?.textSize = textSize
            it.text?.run {
                val x = it.x
                val y = it.y + textSize
                canvas.drawText(this, x, y, textPaint!!)
            }
        }

        // 画path
        markData?.list?.find { it.type == MARK_TYPE_DRAWING }?.segments?.forEach {
            pathPaint?.strokeWidth = it.lineWidth
            val path = Path().apply {
                it.points?.forEachIndexed { index, pointF ->
                    if (index == 0) {
                        moveTo(pointF.x, pointF.y)
                    } else {
                        lineTo(pointF.x, pointF.y)
                    }
                }
            }
            canvas.drawPath(path, pathPaint!!)
        }
    }

}
