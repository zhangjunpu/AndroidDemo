package com.junpu.record.player

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import com.junpu.log.L
import com.junpu.log.logStackTrace
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * AudioTrackPlayer
 * @author junpu
 * @date 2019-10-31
 */
class AudioTrackPlayer : IRecordPlayer {

    private val sampleRateInHz = 44100 // 采样率
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO // 输出声道
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT // 格式
    private val minBufferSize =
        AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
    private var audioTrack: AudioTrack? = null
    private var executorService: ExecutorService? = null

    /**
     * 是否正在播放
     */
    override var isPlaying = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val format = AudioFormat.Builder()
            .setSampleRate(sampleRateInHz)
            .setChannelMask(channelConfig)
            .setEncoding(audioFormat)
            .build()
        audioTrack = AudioTrack(
            audioAttributes,
            format,
            minBufferSize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
    }

    /**
     * 播放
     */
    override fun play(path: String, callback: () -> Unit) {
        if (path.isBlank() || !File(path).exists()) return
        audioTrack?.play()
        isPlaying = true
        L.out("AudioTrackPlayer.play -> start: $isPlaying")
        if (executorService == null) executorService = Executors.newSingleThreadExecutor()
        executorService?.execute {
            var fos: FileInputStream? = null
            try {
                fos = File(path).inputStream()
                val buffer = ByteArray(minBufferSize)
                while (fos.available() > 0) {
                    val readCount = fos.read(buffer)
                    if (readCount != AudioTrack.ERROR_INVALID_OPERATION && readCount != AudioTrack.ERROR_BAD_VALUE
                        && readCount != 0 && readCount != -1
                    ) {
                        audioTrack?.write(buffer, 0, minBufferSize)
                    }
                }
            } catch (e: Exception) {
                e.logStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.logStackTrace()
                }
            }
            Handler(Looper.getMainLooper()).post { callback() }
            isPlaying = false
            L.out("AudioTrackPlayer.play -> end: $isPlaying")
        }
    }

    /**
     * 停止
     */
    override fun stop() {
        try {
            audioTrack?.stop()
            isPlaying = false
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 释放
     */
    override fun release() {
        audioTrack?.release()
        audioTrack = null
    }

}