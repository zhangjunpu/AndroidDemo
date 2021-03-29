package com.junpu.record.player

/**
 * 录音播放器
 * @author junpu
 * @date 2019-10-31
 */
interface IRecordPlayer {

    /**
     * 是否正在播放
     */
    val isPlaying: Boolean

    /**
     * 开始播放
     */
    fun play(path: String, callback: () -> Unit)

    /**
     * 停止播放
     */
    fun stop()

    /**
     * 释放
     */
    fun release()
}