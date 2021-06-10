package com.junpu.customview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.R
import com.junpu.customview.view.daily.DailyWeakKnowledgeView
import kotlinx.android.synthetic.main.activity_daily_charts.*

/**
 * 日报图表
 * @author junpu
 * @date 2021/6/1
 */
class DailyActivity : AppCompatActivity() {

    private val chartData = arrayListOf(
        DailyChart(1578326400000, 3, 89),
        DailyChart(1578672000000, 0, null),
        DailyChart(1579017600000, 3, null),
        DailyChart(1579104000000, 1, null),
        DailyChart(1579190400000, 2, 100),
        DailyChart(1579449600000, 3, 25),
        DailyChart(1580745600000, 6, 50),
        DailyChart(1580918400000, 7, 10),
        DailyChart(1640156651000, 6, 63),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_charts)
        chartView?.setData(chartData)
        errorCauseView?.setData(1, "运算错误")
        layoutLinear?.addView(DailyWeakKnowledgeView(this).apply {
            setData(DailyKnowledgeInfo("10以内的加减法", 7), 8)
        })
    }
}


data class DailyChart(
    var date: Long,
    var count: Int,
    var percent: Int?,
    var px: Float = 0f,
    var py: Float = 0f,
)


data class DailyKnowledgeInfo(
    var knowledgeName: String?,
    var knowledgeCount: Int,
)