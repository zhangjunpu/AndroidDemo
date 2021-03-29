package com.junpu.record.manager

/**
 * 录音类接口
 * @author junpu
 * @date 2019-10-31
 */
interface IRecordManager {

    /**
     * 开始录音
     */
    fun start(callback: () -> Unit)

    /**
     * 停止录音
     */
    fun stop(callback: (duration: Long, path: String?) -> Unit)

    /**
     * 取消
     */
    fun cancel()

    /**
     * 释放
     */
    fun release()

    /**
     * 获取麦克风音频振幅
     */
    fun getMicAmplitude(): Int

    /**
     * 获取录音文件路径
     */
    fun getAudioPath(): String?

    /**
     * 清理音频路径
     */
    fun clearAudioPath()
}