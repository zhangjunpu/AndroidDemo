package com.junpu.customview.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.junpu.customview.R
import kotlin.math.max
import kotlin.math.min

/**
 * 环形进度条
 * @author junpu
 * @date 2019-07-06
 */
class CircleProgressBar : View {

    private val defaultMinWidth = dp2px(50) // 默认最小宽度为50dp
    private val defaultMinHeight = dp2px(50) // 默认最小高度为50dp
    private val defaultProgressBackgroundColor = Color.parseColor("#D0D0D0") // 默认进度条背景色
    private val defaultProgressColor = Color.parseColor("#16BA74") // 默认进度条颜色
    private val defaultMax = 100 // 默认最大进度
    private val defaultProgressWidth = dp2px(10).toFloat() // 默认进度条宽度10dp

    private lateinit var paint: Paint

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var radius: Float = 0f
    private val rectF = RectF()
    private var objectAnimator: ObjectAnimator? = null

    /**
     * 背景颜色
     */
    var progressBackgroundColor = defaultProgressBackgroundColor
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 进度颜色
     */
    var progressColor = defaultProgressColor
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 加载进度
     */
    var progress = 0
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 最大进度
     */
    var max = defaultMax
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 进度条宽度
     */
    var progressWidth = defaultProgressWidth // 进度条宽度
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(attrs, defStyleAttr, defStyleRes)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.CircleProgressBar,
            defStyleAttr,
            defStyleRes
        )
        progressBackgroundColor = a.getColor(
            R.styleable.CircleProgressBar_progressBackgroundColor,
            defaultProgressBackgroundColor
        )
        progressColor =
            a.getColor(R.styleable.CircleProgressBar_progressColor, defaultProgressColor)
        progress = a.getInt(R.styleable.CircleProgressBar_progress, 0)
        max = a.getInt(R.styleable.CircleProgressBar_max, defaultMax)
        progressWidth =
            a.getDimension(R.styleable.CircleProgressBar_progressWidth, defaultProgressWidth)
        a.recycle()

        paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val minWidth = max(minimumWidth, defaultMinWidth)
        val minHeight = max(minimumHeight, defaultMinHeight)
        when {
            widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST -> setMeasuredDimension(
                minWidth,
                minHeight
            )
            widthSpecMode == MeasureSpec.AT_MOST -> setMeasuredDimension(minWidth, heightSpecSize)
            heightSpecMode == MeasureSpec.AT_MOST -> setMeasuredDimension(widthSpecSize, minHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val width = width - paddingStart - paddingEnd.toFloat()
        val height = height - paddingTop - paddingBottom.toFloat()
        progressWidth = min(width, height) / 5
        radius = min(width, height) / 2 - progressWidth / 2 // 半径要减去画笔宽度的一半
        centerX = paddingStart + width / 2
        centerY = paddingTop + height / 2

        rectF.left = centerX - radius
        rectF.right = centerX + radius
        rectF.top = centerY - radius
        rectF.bottom = centerY + radius
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.strokeWidth = progressWidth

        // 画空心圆
        paint.color = progressBackgroundColor
        canvas?.drawCircle(centerX, centerY, radius, paint)

        // 画空心圆弧
        paint.color = progressColor
        canvas?.drawArc(rectF, -90f, progress / max.toFloat() * 360, false, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        objectAnimator?.cancel()
    }

    /**
     * 采用动画形式设置进度
     */
    fun setProgressByAnimation(progress: Int) {
        objectAnimator = ObjectAnimator.ofInt(this, "progress", progress).apply {
            duration = 300L
            start()
        }
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}