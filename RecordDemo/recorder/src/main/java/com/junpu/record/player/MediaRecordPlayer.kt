package com.junpu.record.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.junpu.log.L
import com.junpu.log.logStackTrace

/**
 * MediaPlayer管理
 * @author junpu
 * @date 2019-10-17
 */
class MediaRecordPlayer : IRecordPlayer {

    private var mediaPlayer: MediaPlayer? = null

    init {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setOnErrorListener { _, what, extra ->
                L.e("MediaRecordPlayer.playSound OnError -> what: ${what}, $extra")
                mediaPlayer?.reset()
                false
            }
            setOnPreparedListener {
                start()
            }
        }
    }

    /**
     * 是否正在播放
     */
    override val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    /**
     * 播放音乐
     */
    override fun play(path: String, callback: () -> Unit) {
        try {
            mediaPlayer?.run {
                reset()
                setOnCompletionListener { callback() }
                setDataSource(path)
                prepareAsync()
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 停止播放
     */
    override fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 释放资源
     */
    override fun release() {
        stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
