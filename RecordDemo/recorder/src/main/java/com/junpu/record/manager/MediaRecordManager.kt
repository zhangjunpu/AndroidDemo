package com.junpu.record.manager

import android.media.MediaRecorder
import com.junpu.log.logStackTrace
import com.junpu.record.MAX_RECORD_DURATION
import com.junpu.record.RecordCache
import com.junpu.record.utils.AudioUtils
import java.io.File
import kotlin.math.log10

/**
 * MediaRecord录音
 * @author junpu
 * @date 2019-10-18
 */
class MediaRecordManager : IRecordManager {

    companion object {
        private const val MAX_AMPLITUDE_LEVEL = 8 // 最大声音等级
        private const val AMPLITUDE_BASE = 1
    }

    private var recorder: MediaRecorder? = null
    private var outputPath: String? = null

    /**
     * 初始化音频路径
     */
    private fun initAudioPath() {
        val dirPath = RecordCache.audioCachePath
        if (dirPath.isNullOrBlank()) return
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        } else if (dir.isFile) {
            dir.delete()
            dir.mkdirs()
        }
        outputPath = dir.absolutePath + File.separator + "audio.mp3"
    }

    /**
     * 初始化MediaRecorder
     */
    override fun start(callback: () -> Unit) {
        initAudioPath()
        try {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // 这两个顺序写反会报错
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputPath)
                setMaxDuration(MAX_RECORD_DURATION * 1000)
                prepare()
                start()
            }
            callback()
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 停止录音
     */
    override fun stop(callback: (duration: Long, path: String?) -> Unit) {
        release()
        val duration = AudioUtils.getDuration(outputPath)
        callback(duration, outputPath)
    }

    /**
     * 取消录音
     */
    override fun cancel() {
        release()
        outputPath = null
    }

    /**
     * 释放录音
     */
    override fun release() {
        try {
            recorder?.run {
                stop()
                reset()
                release()
            }
            recorder = null
        } catch (e: Exception) {
            e.logStackTrace()
            try {
                recorder?.run {
                    reset()
                    release()
                }
            } catch (e: Exception) {
                e.logStackTrace()
            }
        }
    }

    /**
     * 更新话筒状态
     */
    override fun getMicAmplitude(): Int {
        var amplitude = 0
        try {
            amplitude = recorder?.maxAmplitude ?: 0
        } catch (e: Exception) {
            e.logStackTrace()
        }
        if (amplitude > 1) {
            val ratio = amplitude.toDouble() / AMPLITUDE_BASE
            var db = 0.0 // 分贝
            if (ratio > 1) db = 20 * log10(ratio)
            return when {
                db <= 40 -> 1
                db <= 48 -> 2
                db <= 56 -> 3
                db <= 64 -> 4
                db <= 72 -> 5
                db <= 80 -> 6
                db <= 88 -> 7
                db > 88 -> 8
                else -> 0
            }
        }
        return 0
    }

    /**
     * 获取输出的音频文件路径
     */
    override fun getAudioPath(): String? {
        return outputPath
    }

    /**
     * 清空音频路径
     */
    override fun clearAudioPath() {
        outputPath = null
    }

}