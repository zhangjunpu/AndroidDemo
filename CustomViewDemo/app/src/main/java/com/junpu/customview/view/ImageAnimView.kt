package com.junpu.customview.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.forEachIndexed
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.junpu.utils.dip
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

private val AVATAR_SIZE = 48.dip // 头像尺寸
private val AVATAR_OFFSET = 36.dip // 头像间偏移距离
private const val SHOW_COUNT = 3 // 显示个数

/**
 * 图片组轮播动画
 * @author : Junpu
 * @data : 2022/7/20
 */
class ImageAnimView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var firstIndex = 0 // 当前首 child 下标
    private val lastChildLeft by lazy { (childCount - 1) * AVATAR_OFFSET.toFloat() } // 最后的 View 的位置
    private var disposable: Disposable? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childMeasureSpec = MeasureSpec.makeMeasureSpec(AVATAR_SIZE, MeasureSpec.EXACTLY)
        measureChildren(childMeasureSpec, childMeasureSpec)
        setMeasuredDimension(AVATAR_SIZE + AVATAR_OFFSET * (SHOW_COUNT - 1), AVATAR_SIZE)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        forEachIndexed { i, view ->
            val left = i * AVATAR_OFFSET
            view.layout(left, 0, left + AVATAR_SIZE, AVATAR_SIZE)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 初始化 alpha
        for ((tmp, i) in (firstIndex until firstIndex + childCount).withIndex()) {
            getChildAt(i % childCount).alpha = if (tmp < SHOW_COUNT) 1f else 0f
        }
    }

    fun setData(list: List<String>) {
        forEachIndexed { i, view ->
            view as ImageView
            Glide.with(context)
                .load(list[i])
                .transform(CircleCrop())
                .into(view)
        }
    }

    /**
     * 下一帧动画
     */
    fun nextFrame() {
        val first = firstIndex
        val showIndex = (first + SHOW_COUNT) % childCount
        forEachIndexed { i, view ->
            // z轴位置
            view.animate()
                .translationXBy(-AVATAR_OFFSET.toFloat())
                .alpha(
                    when (i) {
                        first -> 0f
                        showIndex -> 1f
                        else -> view.alpha
                    }
                )
                .withEndAction {
                    if (i == first) view.x = lastChildLeft
                    view.z = (i - firstIndex).let { if (it < 0) it + childCount else it }.toFloat()
                }
        }
        firstIndex = if (first == childCount - 1) 0 else first + 1
    }

    /**
     * 定时开启(秒)
     */
    fun startAnim(period: Long = 3L) {
        disposable = Observable.interval(period, TimeUnit.SECONDS)
            .subscribe {
                nextFrame()
            }
    }

    /**
     * 定时关闭
     */
    fun stopAnim() {
        disposable?.dispose()
        disposable = null
    }
}