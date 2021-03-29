package com.junpu.customview.bean

import android.graphics.PointF

/**
 * 作业批改Bean
 * @author junpu
 * @date 2019-10-16
 */
data class CorrectMarkBean(
    var version: Int,
    var seq: Int,
    var list: List<CorrectMarkItem>?
)

data class CorrectMarkItem(
    var type: String?, // mark类型
    var seq: Int,
    var x: Float,
    var y: Float,
    var scale: Float,
    var rotation: Int,
    var version: Int,
    var symbol: String?, // 符号类型，对错
    var width: Int,
    var text: String?,
    var segments: List<Segment>?,
    var owner: Any?
)

data class Segment(
    var points: List<PointF>?,
    var lineWidth: Float
)