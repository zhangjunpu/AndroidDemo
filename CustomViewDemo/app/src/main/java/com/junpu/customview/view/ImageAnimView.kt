package com.junpu.customview.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.forEachIndexed
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.junpu.customview.R
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

    private var firstIndex = 2 // 当前首 child 下标
    private val lastChildLeft by lazy { childCount * avatarOffset.toFloat() } // 最后的 View 的位置
    private var disposable: Disposable? = null

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
        setMeasuredDimension(avatarSize + avatarOffset * (showCount + 1), avatarSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        forChildFromIndex(firstIndex) { i, view ->
            val left = (i + 1) * avatarOffset
            view.layout(left, 0, left + avatarSize, avatarSize)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 初始化 alpha
        forChildFromIndex(firstIndex) { i, view -> view.alpha = if (i < showCount) 1f else 0f }
    }

    /**
     * 从 first 开始，遍历列表
     */
    private fun forChildFromIndex(first: Int, callback: (i: Int, view: View) -> Unit) {
        for ((i, item) in (first until first + childCount).withIndex()) {
            callback(i, getChildAt(item % childCount))
        }
    }

    /**
     * 根据 index，返回他在队列中的位置
     */
    private fun getCurrentIndex(index: Int, size: Int = childCount) = (index - firstIndex).let { if (it < 0) it + size else it }

    fun setData(list: List<String>) {
        showCount = min(showCount, list.size)
        removeAllViews()
        list.forEachIndexed { index, item ->
            val i = getCurrentIndex(index, list.size)
            newAvatarImageView(item).apply {
                z = i.toFloat()
                alpha = if (i < showCount) 1f else 0f
                addView(this)
            }
        }
        stopAnim()
        startAnim()
    }

    private fun newAvatarImageView(url: String) = AvatarImageView(context).apply {
        Glide.with(context)
            .load(url)
            .transform(CircleCrop())
            .into(this)
    }

    /**
     * 下一帧动画
     */
    fun nextFrame() {
        val first = firstIndex
        val showIndex = (first + showCount) % childCount // 需要显示的index
        forEachIndexed { i, view ->
            // z轴位置
            view.animate()
                .translationXBy(-avatarOffset.toFloat())
                .alpha(
                    when (i) {
                        first -> 0f // 第一个消失
                        showIndex -> 1f // 最后一个显示
                        else -> view.alpha
                    }
                )
                .withEndAction {
                    if (i == first) view.x = lastChildLeft
                    ViewCompat.setZ(view, getCurrentIndex(i).toFloat()) // 保持队列z值:0,1,2,3,4
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