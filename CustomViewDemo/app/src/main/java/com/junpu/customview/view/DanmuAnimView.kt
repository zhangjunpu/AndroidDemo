package com.junpu.customview.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import com.junpu.customview.R
import com.junpu.utils.dip
import kotlin.concurrent.thread

private val SPACE_W = 6.dip
private val SPACE_H = 8.dip

/**
 * 弹幕
 * @author Junpu
 * @date 2022/9/15
 */
class DanmuAnimView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private val childList1 = mutableListOf<TextView>()
    private val childList2 = mutableListOf<TextView>()
    private val icons = arrayOf(
        R.mipmap.ic_danmu_1,
        R.mipmap.ic_danmu_2,
        R.mipmap.ic_danmu_3,
        R.mipmap.ic_danmu_4,
        R.mipmap.ic_danmu_5,
        R.mipmap.ic_danmu_6,
        R.mipmap.ic_danmu_7,
        R.mipmap.ic_danmu_8,
    )
    private var isLayout = false
    private var isStop = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        children.forEach { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val mid = (childCount + 1) / 2
        var left = width
        var top = 13.dip
        forEachIndexed { i, view ->
            if (i == mid) {
                top += view.measuredHeight + SPACE_H
                left = width
            }
            view.layout(left, top, left + view.measuredWidth, top + view.measuredHeight)
        }
        isLayout = true
    }

    fun setData(data: List<String>) {
        isLayout = false
        val mid = (data.size + 1) / 2
        removeAllViews()
        data.forEachIndexed { i, s ->
            if (i < mid) {
                val child = newChild(s, icons[i % icons.size], 0xAE000000)
                addView(child)
                childList1.add(child)
            } else {
                val child = newChild(s, icons[i % icons.size], 0x3D000000)
                addView(child)
                childList2.add(child)
            }
        }
        invalidate()
    }

    private fun newChild(text: String, imgRes: Int, color: Long): TextView = TextView(context).apply {
        this.text = text
        setTextColor(color.toInt())
        textSize = 11f
        setCompoundDrawablesWithIntrinsicBounds(imgRes, 0, 0, 0)
        compoundDrawablePadding = 3f.dip
    }

    private fun startAnim(list: List<View>, index: Int = 0, duration: Long) {
        val w = width + list[0].width.toFloat()
        val size = list.size
        val view = list[index % size]
        val dest = width + view.width.toFloat()
        var flag = true
        view.animate()
            .translationXBy(-dest)
            .setDuration((dest / w * duration).toLong())
            .setInterpolator(LinearInterpolator())
            .setUpdateListener {
                val value = (view.width + SPACE_W) / dest
                if (flag && it.animatedFraction > value) {
                    flag = false
                    startAnim(list, index + 1, duration)
                }
            }
            .withEndAction {
                view.x = width.toFloat()
            }
        view.animation
    }

    fun startAnim() {
        isStop = false
        thread {
            while (!isLayout) {
                Thread.sleep(100)
            }
            if (isStop) return@thread
            post {
                startAnim(childList1, 0, 7000)
                startAnim(childList2, 0, 6000)
            }
        }
    }

    fun stopAnim() {
        isStop = true
        children.forEach {
            it.animate().cancel()
            it.x = width.toFloat()
        }
    }
}