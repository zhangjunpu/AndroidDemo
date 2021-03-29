package com.junpu.record.manager

import android.media.AudioRecord
import com.junpu.log.L
import com.junpu.log.logStackTrace
import com.junpu.record.CONVERT_SUCCESS
import com.junpu.record.RecordCache
import com.junpu.record.utils.AudioUtils
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

/**
 * AudioRecord录音
 * @author junpu
 * @date 2019-10-28
 */
class AudioRecordManager : IRecordManager {

    private val config: AudioRecordConfig by lazy { AudioRecordConfig() }
    private var audioRecord: AudioRecord? = null
    private var audioCodec: AudioCodec? = null
    private var pcmPath: String? = null
    private var outputPath: String? = null
    private var aacPath: String? = null
    private var isRecording = false // 录音状态

    init {
        audioCodec = AudioCodec(config)
    }

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
        pcmPath =
            dir.absolutePath + File.separator + "audio" + AudioRecordConfig.RecordFormat.PCM.format
        outputPath =
            dir.absolutePath + File.separator + "audio" + AudioRecordConfig.RecordFormat.WAV.format
        aacPath =
            dir.absolutePath + File.separator + "audio" + AudioRecordConfig.RecordFormat.M4A.format
    }

    /**
     * 开始录音
     */
    override fun start(callback: () -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            resetFile(pcmPath)
            var dos: DataOutputStream? = null
            var dosAac: DataOutputStream? = null
            try {
                initAudioPath()
                audioRecord = AudioRecord(
                    config.audioSource, config.sampleRateInHz, config.channelConfig,
                    config.audioFormat, config.minBufferSize
                ).apply {
                    startRecording()
                    isRecording = true
                    callback()
                }
                dos = DataOutputStream(BufferedOutputStream(File(pcmPath ?: "").outputStream()))
                dosAac = DataOutputStream(File(aacPath ?: "").outputStream())
                val buffer = ShortArray(config.minBufferSize)
                while (isRecording) {
                    val read = audioRecord!!.read(buffer, 0, config.minBufferSize)
                    L.out("AudioRecordManager.start -> read: ${read}")
                    if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                        for (i in 0 until read) {
                            val gain = AudioUtils.gain(buffer[i].toInt(), 2.0f)
                            val short = AudioUtils.reverseBytes(gain)
                            dos.writeShort(short.toInt())
//                            buffer[i] = short
//                            audioCodec?.encodeAsync(short, dosAac)
                        }
                    }
                }
            } catch (e: Exception) {
                e.logStackTrace()
            } finally {
                try {
                    dos?.close()
                    dosAac?.close()
                } catch (e: IOException) {
                    e.logStackTrace()
                }
            }
        }
    }

    /**
     * 停止录音
     */
    override fun stop(callback: (duration: Long, path: String?) -> Unit) {
        if (isRecording) {
            release()
            audioCodec?.stop()
        }
        AudioUtils.convertToWave(pcmPath, outputPath, config) {
            L.out("AudioRecordManager.stop -> convert to wav: $it")
            if (it == CONVERT_SUCCESS) {
                val duration = AudioUtils.getDuration(outputPath)
                callback(duration, outputPath)
            } else {
                callback(0, pcmPath)
            }
        }
    }

    /**
     * 取消录音
     */
    override fun cancel() {
        if (isRecording) {
            release()
        }
        pcmPath = null
    }

    /**
     * 释放资源
     */
    override fun release() {
        try {
            isRecording = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    /**
     * 获取音频振幅
     */
    override fun getMicAmplitude(): Int {
        return 0
    }

    /**
     * 获取wav输出的音频文件路径
     */
    override fun getAudioPath(): String? = outputPath

    /**
     * 清理音频路径
     */
    override fun clearAudioPath() {
        pcmPath = null
        outputPath = null
    }

    /**
     * 重新生成新的文件
     */
    private fun resetFile(path: String?) {
        if (path.isNullOrBlank()) return
        File(path).run {
            if (exists()) delete()
            createNewFile()
        }
    }
}