package com.junpu.customview.html

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.text.Html.ImageGetter
import android.util.Log
import android.view.View
import android.widget.TextView
import org.sufficientlysecure.htmltextview.HtmlTextView
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * HttpImageGetter
 * @author junpu
 * @date 2020-01-19
 */
class HtmlImageGetter(private val container: TextView, private val matchParentWidth: Boolean = false) : ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val urlDrawable = UrlDrawable()
        val asyncTask = ImageGetterAsyncTask(urlDrawable, this, container, matchParentWidth)
        asyncTask.execute(source)
        return urlDrawable
    }

    private class ImageGetterAsyncTask(
        private val urlDrawable: UrlDrawable?,
        private val imageGetter: HtmlImageGetter?,
        private val container: View?,
        private val matchParentWidth: Boolean = false
    ) : AsyncTask<String?, Void?, Drawable?>() {

        private val resources = container?.resources
        private var source: String? = null
        private var scale = 0f

        override fun doInBackground(vararg params: String?): Drawable? {
            source = params[0]
            return if (resources != null) fetchDrawable(resources, source) else null
        }

        override fun onPostExecute(result: Drawable?) {
            if (result == null) {
                Log.w(HtmlTextView.TAG, "Drawable result is null! (source: $source)")
                return
            }
            urlDrawable?.run {
                val width = (result.intrinsicWidth * scale).toInt()
                val height = (result.intrinsicHeight * scale).toInt()
                setBounds(0, 0, width, height)
                drawable = result
            }
            imageGetter?.container?.run {
                invalidate()
                text = text
            }
        }

        /**
         * Get the Drawable from URL
         */
        fun fetchDrawable(res: Resources?, urlString: String?): Drawable? {
            return try {
                BitmapDrawable(res, fetchUrl(urlString)).apply {
                    scale = getScale(this)
                    println("BitmapDrawable = ${intrinsicWidth}, $intrinsicHeight")
                    setBounds(0, 0, (intrinsicWidth * scale).toInt(), (intrinsicHeight * scale).toInt())
                }
            } catch (e: Exception) {
                null
            }
        }

        private fun getScale(drawable: Drawable): Float {
            if (!matchParentWidth || container == null) return 1f
            val maxWidth = container.width.toFloat()
            val originalDrawableWidth = drawable.intrinsicWidth.toFloat()
            return maxWidth / originalDrawableWidth
        }

        private fun fetchUrl(url: String?): Bitmap? {
            val imageUrl = URL(url)
            try {
                val conn = imageUrl.openConnection() as HttpURLConnection
                conn.doInput = true
                conn.connect()
                val inputStream = conn.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                println("bitmap = ${bitmap.width}, ${bitmap.height}")
                inputStream.close()
                return bitmap
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    inner class UrlDrawable : BitmapDrawable() {
        var drawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }
    }
}