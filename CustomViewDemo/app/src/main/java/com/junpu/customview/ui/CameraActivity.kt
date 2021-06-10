package com.junpu.customview.ui

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.R
import com.junpu.utils.app
import com.junpu.utils.backgroundColor
import com.junpu.utils.dip

/**
 *
 * @author junpu
 * @date 2021/6/8
 */
class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
            addView(CameraView(this@CameraActivity), lp)
            val line = TextView(this@CameraActivity).apply {
                backgroundColor = Color.parseColor("#dddddd")
            }
            addView(line, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip(1)))
            addView(CameraView1(this@CameraActivity), lp)
        }
        setContentView(layout)
    }
}


private const val BITMAP_SIZE = 500
private val bitmap by lazy {
    val option = BitmapFactory.Options()
    option.inJustDecodeBounds = true
    BitmapFactory.decodeResource(app.resources, R.mipmap.cat, option)
    option.inJustDecodeBounds = false
    option.inDensity = option.outWidth
    option.inTargetDensity = BITMAP_SIZE
    BitmapFactory.decodeResource(app.resources, R.mipmap.cat, option)
}

class CameraView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var camera = Camera()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val bw = bitmap.width
        val bh = bitmap.height

        canvas.save()
        camera.save()
        canvas.translate(cx, cy)
        camera.rotateY(30f)
        camera.applyToCanvas(canvas)
        canvas.translate(-cx, -cy)
        canvas.clipRect(cx - bw / 2f, cy - bh / 2f, cx, cy + bh / 2f)
        canvas.drawBitmap(bitmap, cx - bw / 2, cy - bh / 2f, null)
        camera.restore()
        canvas.restore()

        canvas.save()
        canvas.clipRect(cx, cy - bh / 2f, cx + bw / 2f, cy + bh / 2f)
        canvas.drawBitmap(bitmap, cx - bw / 2, cy - bh / 2f, null)
        canvas.restore()
    }
}


class CameraView1 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var camera = Camera()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val bw = bitmap.width
        val bh = bitmap.height

        val w = bw / 2f
        val h = bh / 2f

        canvas.save()
        camera.save()
        canvas.translate(cx, cy)
        canvas.rotate(-30f)
        camera.rotateX(50f)
        camera.applyToCanvas(canvas)
        canvas.clipRect(-w * 2, 0f, w * 2, h * 2)
        canvas.rotate(30f)
        canvas.translate(-cx, -cy)
        canvas.drawBitmap(bitmap, cx - w, cy - h, null)
        camera.restore()
        canvas.restore()

        canvas.save()
        camera.save()
        canvas.translate(cx, cy)
        canvas.rotate(-30f)
        camera.applyToCanvas(canvas)
        canvas.clipRect(-w * 2, -h * 2, w * 2, 0f)
        canvas.rotate(30f)
        canvas.translate(-cx, -cy)
        canvas.drawBitmap(bitmap, cx - w, cy - h, null)
        camera.restore()
        canvas.restore()
    }
}