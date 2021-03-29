package com.junpu.record.manager

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

/**
 * AudioRecord配置
 * @author junpu
 * @date 2019-11-04
 */
class AudioRecordConfig {

    /**
     * 输入源
     */
    val audioSource = MediaRecorder.AudioSource.MIC

    /**
     * 采样率 8000/16000/44100
     */
    val sampleRateInHz = 44100

    /**
     * 比特率
     */
    val bitRate = 64000

    /**
     * 声道，单声道 See[AudioFormat.CHANNEL_IN_MONO]，多声道 See [AudioFormat.CHANNEL_IN_STEREO]
     */
    val channelConfig = AudioFormat.CHANNEL_IN_MONO

    /**
     * 编码格式：8bit See [AudioFormat.ENCODING_PCM_8BIT]，16bit See [AudioFormat.ENCODING_PCM_16BIT]
     */
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    /**
     * 缓冲大小
     */
    var minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)

    /**
     * 当前的声道数
     */
    val numChannels: Int
        get() = when (channelConfig) {
            AudioFormat.CHANNEL_IN_MONO -> 1
            AudioFormat.CHANNEL_IN_STEREO -> 2
            else -> 0
        }

    /**
     * 获取当前录音的采样位宽 单位bit
     */
    val bitsPerSample: Int
        get() = when (audioFormat) {
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            else -> 0
        }

    /**
     * 录音格式
     */
    enum class RecordFormat(val format: String) {
        PCM(".pcm"),
        WAV(".wav"),
        M4A(".m4a")
    }
}