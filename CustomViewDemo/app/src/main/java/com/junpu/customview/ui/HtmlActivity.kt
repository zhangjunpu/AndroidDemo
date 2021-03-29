package com.junpu.customview.ui

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.ImageGetter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.junpu.customview.R
import com.junpu.customview.html.GlideImageGetter
import com.junpu.customview.html.HtmlImageGetter
import kotlinx.android.synthetic.main.activity_html.*
import org.sufficientlysecure.htmltextview.HtmlFormatter
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder
import org.sufficientlysecure.htmltextview.HtmlTextView


/**
 *
 * @author junpu
 * @date 2020-01-03
 */
class HtmlActivity : AppCompatActivity() {

    private val htmlText =
        "<p>东东受凉咳嗽得厉害，医生给他开了瓶久咳丸。</p><p><img src=\"https://homeworkdone-rtf-test.oss-cn-beijing.aliyuncs.com/e948fb6f27824e7dbc02f20451480613/7eb9bcff-c150-4f6c-8823-fbb953d018cd\" alt=\"\"/></p><p style=\\\"white-space: normal;\\\">要解决“这瓶久咳丸够九岁的东东吃几天？”这个问题，需要的信息是（&nbsp; &nbsp; &nbsp;）</p><p style=\\\"white-space: normal;\\\">A. 60粒， 2粒， 2次</p><p style=\\\"white-space: normal;\\\">B.&nbsp;60粒， 128毫克， 2次</p><p style=\\\"white-space: normal;\\\">C.&nbsp;60粒， 6粒， 2次</p><p style=\\\"white-space: normal;\\\">D. 题目中的所有信息</p><p><br/></p>"

    private val htmlText2 =
        "<img alt=\"菁优网\" src=\"http://img.jyeoo.net/quiz/images/201911/142/c2d8f792.png\" style=\"vertical-align:middle;FLOAT:right\" />在平面图上标出校园内各建筑物的位置．<br />（1）食堂在校门正西方向100m处．<br />（2）图书室在校门的东偏北30°方向150m处．"

    private val htmlText3 =
        "把符合要求的四边形的序号填入横线里．<br />①长方形&nbsp;&nbsp;&nbsp;&nbsp;②正方形&nbsp;&nbsp;&nbsp;&nbsp;③平行四边形&nbsp;&nbsp;&nbsp;&nbsp;④梯形<br />（1）只有一组对边平行．<!--BA--><div class=\"quizPutTag\" c=\"true\"></div><!--EA--><br />（2）四条边相等，四个角都是直角．<!--BA--><div class=\"quizPutTag\" c=\"true\"></div><!--EA--><br />（3）两组对边分别平行，没有直角．<!--BA--><div class=\"quizPutTag\" c=\"true\"></div><!--EA-->"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html)

        val httpImageGetter = HtmlImageGetter(textHtml)
        val glideImageGetter = GlideImageGetter(textHtml)

        textView?.append(Html.fromHtml("<p style=\"color:red;\">小客车第三次</p>"))
        textHtml?.appendHtml(htmlText3)
        textHtml?.append("\n")
        textHtml?.appendLineHtml(htmlText2, httpImageGetter)
        textHtml?.appendLineHtml(htmlText, httpImageGetter)
        textHtml?.appendLineHtml(htmlText, glideImageGetter)
        textHtml?.appendLineHtml(htmlText2, glideImageGetter)


        imageView?.setImageResource(R.mipmap.ic_image)
        val imageSrc =
            "https://homeworkdone-rtf-test.oss-cn-beijing.aliyuncs.com/e948fb6f27824e7dbc02f20451480613/7eb9bcff-c150-4f6c-8823-fbb953d018cd"
        Glide.with(this)
            .load(imageSrc)
            .centerInside()
            .into(imageHtml)
    }
}

fun HtmlTextView.appendHtml(html: String, imageGetter: ImageGetter? = null) = apply {
    val builder = HtmlFormatterBuilder()
        .setHtml(html)
        .setImageGetter(imageGetter)
    append(HtmlFormatter.formatHtml(builder))
}

fun HtmlTextView.appendLineHtml(html: String, imageGetter: ImageGetter? = null) = apply {
    appendHtml(html, imageGetter).append("\n")
}