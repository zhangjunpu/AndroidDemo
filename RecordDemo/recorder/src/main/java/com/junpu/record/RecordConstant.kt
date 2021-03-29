@file:JvmName("RecordConstant")

package com.junpu.record

/**
 * 录音模块配置参数
 * @author junpu
 * @date 2019-10-18
 */

// 录音内部状态
const val STATUS_NONE = 0 // 默认状态
const val STATUS_RECORD_PREPARE = 1 // 按下按钮
const val STATUS_RECORD_START = 2 // 开始录音
const val STATUS_RECORDING = 3 // 录音中
const val STATUS_RECORD_WANT_CANCEL = 4 // 想要取消
const val STATUS_RECORD_CANCEL = 5 // 录音取消
const val STATUS_RECORD_COMPLETE = 6 // 录音完成

// 录音回调状态
const val RECORD_ACTION_NONE = -1 // 默认状态
const val RECORD_ACTION_START = 0 // 开始
const val RECORD_ACTION_COMPLETE = 1 // 完成
const val RECORD_ACTION_CANCEL = 2 // 取消
const val RECORD_ACTION_DISABLE = 3 // 不可用

// 录音时长限制
const val MIN_RECORD_DURATION = 5 // 最小录音 5s
const val MAX_RECORD_DURATION = 60 // 最大录音 60s

// 倒计时节点
const val RECORD_COUNTDOWN_NODE = 10 // 最后10s开始倒计时

// 录音类型
const val RECORD_TYPE_MEDIA = 1 // MediaRecorder录音
const val RECORD_TYPE_AUDIO = 2 // AudioRecord录音

// pcm转wav状态
const val CONVERT_SUCCESS = 1
const val CONVERT_FAILURE = 2

// 转码超时
const val CODEC_TIMEOUT = 10000L