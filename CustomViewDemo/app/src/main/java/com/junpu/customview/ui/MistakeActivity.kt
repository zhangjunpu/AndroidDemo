package com.junpu.customview.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.junpu.customview.R
import kotlinx.android.synthetic.main.activity_mistake_chart.*

/**
 *
 * @author junpu
 * @date 2021/6/2
 */
class MistakeActivity : AppCompatActivity() {

    private var chartData = mutableListOf(
        MistakeHomeChartBean(1578326400000, 3),
        MistakeHomeChartBean(1578672000000, 7),
        MistakeHomeChartBean(1579017600000, 2),
        MistakeHomeChartBean(1579104000000, 0),
        MistakeHomeChartBean(1579190400000, 4),
        MistakeHomeChartBean(1579449600000, 13),
        MistakeHomeChartBean(1580745600000, 7),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mistake_chart)
        chartView?.setData(chartData)
    }
}

data class MistakeHomeChartBean(
    val date: Long,
    val count: Int,
)