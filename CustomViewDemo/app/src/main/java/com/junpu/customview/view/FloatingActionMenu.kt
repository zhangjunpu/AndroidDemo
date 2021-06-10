package com.junpu.customview.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.junpu.customview.R
import kotlinx.android.synthetic.main.layout_floating_action_menu.view.*

/**
 * 弹出动画菜单
 * @author junpu
 * @date 2019-07-14
 */
class FloatingActionMenu : FrameLayout {

    //判断是否点击过
    private var state = false
    private var offsetX: Float = 0f // x轴偏移量
    private var offsetY: Float = 0f // y轴偏移量
    private var duration: Long = 200L
    private var listener: OnActionClickListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    init {
        LayoutInflater.from(context).inflate(R.layout.layout_floating_action_menu, this, true)
        btn1?.setOnClickListener {
            if (!state) open() else close()
        }
        btn2?.setOnClickListener {
            listener?.onActionClick(it)
            close()
        }
        btn3?.setOnClickListener {
            listener?.onActionClick(it)
            close()
        }

        val width = btn1.layoutParams.width
        val height = btn1.layoutParams.height
        offsetY = height.toFloat()
        offsetX = width.toFloat() * 0.75f
    }

    /**
     * 展开
     */
    private fun open() {
        btn1?.isClickable = false
        btn2.visibility = View.VISIBLE
        btn3.visibility = View.VISIBLE
        btn2?.animate()?.xBy(-offsetX)?.yBy(-offsetY)?.setDuration(duration)?.start()
        btn3?.animate()?.xBy(offsetX)?.yBy(-offsetY)?.setDuration(duration)?.start()
        postDelayed({
            btn1?.isClickable = true
            btn2?.isClickable = true
            btn3?.isClickable = true
            state = true
        }, duration)
    }

    /**
     * 收缩
     */
    private fun close() {
        btn1?.isClickable = false
        btn2?.isClickable = false
        btn3?.isClickable = false
        btn2?.animate()?.xBy(offsetX)?.yBy(offsetY)?.setDuration(duration)?.start()
        btn3?.animate()?.xBy(-offsetX)?.yBy(offsetY)?.setDuration(duration)?.start()
        postDelayed({
            btn1?.isClickable = true
            state = false
            btn2.visibility = View.INVISIBLE
            btn3.visibility = View.INVISIBLE
        }, duration)
    }

    fun setOnActionClickListener(listener: OnActionClickListener) {
        this.listener = listener
    }

    interface OnActionClickListener {
        fun onActionClick(view: View)
    }

}