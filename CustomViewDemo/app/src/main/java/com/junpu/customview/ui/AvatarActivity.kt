package com.junpu.customview.ui

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.R
import com.junpu.log.L
import kotlin.math.min


/**
 *
 * @author junpu
 * @date 2021/6/1
 */
class AvatarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)
    }
}

class AvatarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private var path = Path()
    private var cx = 0f
    private var cy = 0f
    private var radius = 0f
    private var xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

    var strokeWidth = 0f
        set(value) {
            field = value
            invalidate()
        }
    var strokeColor = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }
    var srcId: Int = 0
    var src: Bitmap? = null

    init {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.AvatarView,
            defStyleAttr,
            0
        )
        strokeColor = a.getColor(R.styleable.AvatarView_strokeColor, Color.TRANSPARENT)
        strokeWidth = a.getDimension(R.styleable.AvatarView_strokeWidth, 0f)
        srcId = a.getResourceId(R.styleable.AvatarView_src, 0)
        a.recycle()
    }

    private fun getBitmap(resId: Int): Bitmap? {
        L.vv("srcId $srcId, $width")
        if (resId != 0) {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeResource(resources, resId, opts)
            opts.inJustDecodeBounds = true
            opts.inDensity = opts.outWidth
            opts.inTargetDensity = width
            return BitmapFactory.decodeResource(resources, resId, opts)
        }
        return null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cx = width / 2f
        cy = height / 2f
        radius = min(width / 2f, height / 2f)
        path.addCircle(width / 2f, height / 2f, radius, Path.Direction.CW)
        src = getBitmap(srcId)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = src ?: return
        val w = width.toFloat()
        val h = height.toFloat()

        val saveCount = canvas.saveLayer(0f, 0f, w, h, paint)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        paint.xfermode = xfermode
        canvas.drawCircle(cx, cy, radius - strokeWidth / 2f, paint)
        paint.xfermode = null
        canvas.restoreToCount(saveCount)

        if (strokeWidth > 0) {
            paint.strokeWidth = strokeWidth
            paint.color = strokeColor
            canvas.drawCircle(cx, cy, radius - strokeWidth / 2f, paint)
        }
    }
}