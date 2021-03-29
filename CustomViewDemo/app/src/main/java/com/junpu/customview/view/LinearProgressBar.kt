package com.junpu.customview.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import com.junpu.customview.R

/**
 * 自定义渐变圆头进度条
 * @author junpu
 * @date 2019-12-20
 */
class LinearProgressBar : View {

    private val defaultMinWidth = dp2px(50) // 默认最小宽度 50dp
    private val defaultMinHeight = dp2px(2) // 默认最小高度 2dp
    private val defaultProgressBackgroundColor = Color.parseColor("#E8E8E8") // 默认背景颜色
    private val defaultProgressColor = Color.parseColor("#7ED321") // 默认进度颜色
    private val defaultMax = 100 // 默认最大进度

    private var animator: ObjectAnimator? = null
    private lateinit var backgroundPaint: Paint // 背景画笔
    private lateinit var progressPaint: Paint // 进度画笔
    private lateinit var headBitmap: Bitmap // 进度头上的阴影
    private lateinit var headMatrix: Matrix

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
     * 是否渐变
     */
    var isGradient = false
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(attrs, defStyleAttr, defStyleRes)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LinearProgressBar, defStyleAttr, defStyleRes)
        progressBackgroundColor = a.getColor(R.styleable.LinearProgressBar_progressBackgroundColor, defaultProgressBackgroundColor)
        progressColor = a.getColor(R.styleable.LinearProgressBar_progressColor, defaultProgressColor)
        progress = a.getInt(R.styleable.LinearProgressBar_progress, 0)
        max = a.getInt(R.styleable.LinearProgressBar_max, defaultMax)
        isGradient = a.getBoolean(R.styleable.LinearProgressBar_isGradient, false)
        a.recycle()

        // 背景画笔
        backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = progressBackgroundColor
        }

        // 进度条画笔
        progressPaint = Paint().apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            color = progressColor
        }

        // 进度头阴影
        headBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_progress_head)
        headMatrix = Matrix()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

        val minWidth = maxOf(minimumWidth, defaultMinWidth)
        val minHeight = maxOf(minimumHeight, defaultMinHeight)
        when {
            widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST -> setMeasuredDimension(minWidth, minHeight)
            widthSpecMode == MeasureSpec.AT_MOST -> setMeasuredDimension(minWidth, heightSpecSize)
            heightSpecMode == MeasureSpec.AT_MOST -> setMeasuredDimension(widthSpecSize, minHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val strokeWidth = height.toFloat()
        backgroundPaint.strokeWidth = strokeWidth
        progressPaint.strokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val startY = height / 2f
        // 画背景
        canvas?.drawLine(0f, startY, width.toFloat(), startY, backgroundPaint)
        // 画进度
        val progressWidth = width * progress / max.toFloat()
        canvas?.drawLine(0f, startY, progressWidth, startY, progressPaint)
        // 画阴影
        if (isGradient && progress in 1 until max) {
            headBitmap.let {
                val bWidth = it.width
                val bHeight = it.height
                val scale = height / bHeight.toFloat()
                canvas?.drawBitmap(headBitmap, headMatrix.apply {
                    setScale(scale, scale)
                    postTranslate(progressWidth - bWidth * scale + height / 2, 0f)
                }, null)
            }
        }
    }

    /**
     * 设置进度带动画
     */
    fun setProgressByAnimator(progress: Int) {
        animator = ObjectAnimator.ofInt(this, "progress", progress).apply {
            interpolator = LinearInterpolator()
            duration = 100L
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
        animator = null
        headBitmap.recycle()
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(1, dp.toFloat(), resources.displayMetrics).toInt()
    }
}