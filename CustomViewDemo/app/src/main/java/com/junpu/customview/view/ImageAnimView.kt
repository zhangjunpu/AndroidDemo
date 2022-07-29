package com.junpu.customview.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.forEachIndexed
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.junpu.customview.R
import com.junpu.log.L
import com.junpu.utils.dip
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.math.min

private val DEFAULT_AVATAR_SIZE = 18.dip // 头像尺寸
private val DEFAULT_AVATAR_OFFSET = 12.dip // 头像间偏移距离
private const val DEFAULT_SHOW_COUNT = 3 // 显示个数

/**
 * 图片组轮播动画
 * @author : Junpu
 * @data : 2022/7/20
 */
class ImageAnimView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var avatarSize = DEFAULT_AVATAR_SIZE // 头像尺寸
    var avatarOffset = DEFAULT_AVATAR_OFFSET // 头像偏移量
    var showCount = DEFAULT_SHOW_COUNT // 最大显示头像个数

    private var firstIndex = 0 // 当前首 child 下标
    private val lastChildLeft by lazy { (childCount - 1) * avatarOffset.toFloat() } // 最后的 View 的位置
    private var disposable: Disposable? = null
    private var avatarCount = 5 // 头像总数

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ImageAnimView).apply {
            avatarSize = getDimensionPixelSize(R.styleable.ImageAnimView_avatarSize, DEFAULT_AVATAR_SIZE)
            avatarOffset = getDimensionPixelSize(R.styleable.ImageAnimView_avatarOffset, DEFAULT_AVATAR_OFFSET)
            showCount = getInt(R.styleable.ImageAnimView_showCount, DEFAULT_SHOW_COUNT)
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childMeasureSpec = MeasureSpec.makeMeasureSpec(avatarSize, MeasureSpec.EXACTLY)
        measureChildren(childMeasureSpec, childMeasureSpec)
        setMeasuredDimension(avatarSize + avatarOffset * (showCount - 1), avatarSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        forEachIndexed { i, view ->
            val left = i * avatarOffset
            view.layout(left, 0, left + avatarSize, avatarSize)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 初始化 alpha
        for ((tmp, i) in (firstIndex until firstIndex + childCount).withIndex()) {
            getChildAt(i % childCount).alpha = if (tmp < showCount) 1f else 0f
        }
    }

    fun setData(list: List<String>) {
        avatarCount = min(list.size, childCount)
        showCount = min(showCount, list.size)
        if (list.size < childCount) {
            for (i in childCount - 1 downTo list.size) {
                removeViewAt(i)
            }
        }
        for (i in 0 until avatarCount) {
            val view = getChildAt(i) as ImageView
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
        val showIndex = (first + showCount) % childCount
        forEachIndexed { i, view ->
            // z轴位置
            view.animate()
                .translationXBy(-avatarOffset.toFloat())
                .alpha(
                    when (i) {
                        first -> 0f
                        showIndex -> 1f
                        else -> view.alpha
                    }
                )
                .withEndAction {
                    if (i == first) view.x = lastChildLeft
                    ViewCompat.setZ(view, (i - firstIndex).let { if (it < 0) it + childCount else it }.toFloat())
                }
        }
        firstIndex = if (first == childCount - 1) 0 else first + 1
    }

    /**
     * 定时开启(秒)
     */
    fun startAnim(period: Long = 2L) {
        if (childCount <= showCount) return
        disposable = Observable.interval(period, TimeUnit.SECONDS)
            .subscribe {
                nextFrame()
            }
    }

    /**
     * 定时关闭
     */
    fun stopAnim() {
        if (childCount <= showCount) return
        disposable?.dispose()
        disposable = null
    }
}