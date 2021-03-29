package com.junpu.customview.html

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.junpu.log.L

/**
 *
 * @author junpu
 * @date 2020-01-19
 */
class GlideImageGetter(private val textView: TextView) : Html.ImageGetter {
    override fun getDrawable(source: String?): Drawable {
        val drawable = UrlDrawable()
        Glide.with(textView)
                .asDrawable()
                .load(source)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        val width = resource.intrinsicWidth
                        val height = resource.intrinsicHeight
                        L.vv("glide resource = $width / $height")
                        drawable.run {
                            urlDrawable = resource
                            urlDrawable?.setBounds(0, 0, width, height)
                            setBounds(0, 0, width, height)
                        }
                        textView.run {
                            invalidate()
                            text = text
                        }
                    }
                })
        return drawable
    }

    inner class UrlDrawable : BitmapDrawable() {
        var urlDrawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            urlDrawable?.draw(canvas)
        }
    }
}