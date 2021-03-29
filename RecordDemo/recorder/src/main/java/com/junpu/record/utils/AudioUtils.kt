package com.junpu.record.utils

import android.media.MediaPlayer
import com.junpu.log.logStackTrace
import com.junpu.record.CONVERT_FAILURE
import com.junpu.record.CONVERT_SUCCESS
import com.junpu.record.manager.AudioRecordConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * 音频工具类
 * @author junpu
 * @date 2019-10-31
 */
object AudioUtils {

    /**
     * 音量增益临界值
     */
    const val GAIN_MARGIN = 4096

    /**
     * 音量增益优化
     * @author guoxiaolei
     */
    fun gain(x: Int, scale: Float): Short {
        val x0 = x * scale
        return when {
            x0 <= -32768 - GAIN_MARGIN -> -32768
            x0 < -32768 + GAIN_MARGIN -> {
                val x1 = x0 + 32768 + GAIN_MARGIN
                ((0.25F / GAIN_MARGIN) * (x1 * x1 + GAIN_MARGIN * 2) - 32768).toInt().toShort()
            }
            x0 <= 32767 - GAIN_MARGIN -> x0.toInt().toShort()
            x0 < 32767 + GAIN_MARGIN -> {
                val x1 = x0 - 32767 - GAIN_MARGIN
                (-(0.25F / GAIN_MARGIN) * (x1 * x1 - GAIN_MARGIN * 2) + 32767).toInt().toShort()
            }
            else -> 32767
        }
    }

    /**
     * Short大小端转换
     */
    fun reverseBytes(i: Short): Short {
        return (i.toInt() and 0xFF00 shr 8 or (i.toInt() shl 8)).toShort()
    }

    /**
     * 获取媒体文件的时长
     */
    fun getDuration(path: String?): Long {
        if (path.isNullOrBlank()) return 0L
        var durationMillis = 0
        try {
            MediaPlayer().apply {
                setDataSource(path)
                prepare()
                durationMillis = duration
                release()
            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
        return durationMillis.toLong()
    }

    /**
     * 获取媒体文件的时长，异步
     */
    fun getDurationAsync(path: String?, callback: (duration: Long) -> Unit) {
        if (path.isNullOrBlank()) return
        try {
            MediaPlayer().apply {
                setOnPreparedListener {
                    callback(it.duration.toLong())
                    release()
                }
                reset()
                setDataSource(path)
                prepareAsync()
            }
        } catch (e: Exception) {
            e.logStackTrace()
            callback(0)
        }
    }

    /**
     * pcm to wave
     * @param config 录音配置
     */
    fun convertToWave(
        inPath: String?,
        outPath: String?,
        config: AudioRecordConfig,
        callback: (status: Int) -> Unit
    ) {
        if (inPath.isNullOrBlank() || outPath.isNullOrBlank()) {
            callback(CONVERT_FAILURE)
            return
        }
        val inFile = File(inPath)
        if (!inFile.exists()) {
            callback(CONVERT_FAILURE)
            return
        }
        val outFile = File(outPath)
        if (outFile.exists()) outFile.delete()
        try {
            outFile.createNewFile()
        } catch (e: IOException) {
            e.logStackTrace()
        }

        val fileLength = inFile.length()

        var fis: FileInputStream? = null
        var fos: FileOutputStream? = null
        try {
            fis = inFile.inputStream()
            fos = outFile.outputStream()
            val header = getWaveHeader(fileLength, config)
            fos.write(header)
//            fos.write(fis.readBytes())
            val data = ByteArray(config.minBufferSize)
            while (fis.read(data) != -1) {
                fos.write(data)
            }
            fis.close()
            fos.close()
            callback(CONVERT_SUCCESS)
        } catch (e: Exception) {
            e.logStackTrace()
            callback(CONVERT_FAILURE)
        } finally {
            try {
                fis?.close()
                fos?.close()
            } catch (e: IOException) {
                e.logStackTrace()
            }
        }
    }

    /**
     * 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，
     * wave是RIFF文件结构，每一部分为一个chunk，
     * 其中有RIFF WAVE chunk， FMT Chunk，Fact chunk,Data chunk,
     * 其中Fact chunk是可以选择的，
     * */
    private fun getWaveHeader(fileLength: Long, config: AudioRecordConfig): ByteArray {
        val sampleRate = config.sampleRateInHz.toLong()
        val numChannels = config.numChannels.toLong()
        val bitsPerSample = config.bitsPerSample.toLong()
        val chunkSize = fileLength + 36
        val subChunk2Size = fileLength// - 44

        val byteRate = sampleRate * numChannels * bitsPerSample / 8
        val blockAlign = numChannels * bitsPerSample / 8
        val subChunk1Size = 16
        val audioFormat = 1

        val header = ByteArray(44)
        // The canonical WAVE format starts with the RIFF header:
        // ChunkID：RIFF
        header[0] = 'R'.toByte() // RIFF
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        // ChunkSize：6 + SubChunk2Size, or 4 + (8 + SubChunk1Size) + (8 + SubChunk2Size)
        header[4] = (chunkSize and 0xff).toByte() // 数据大小
        header[5] = (chunkSize shr 8 and 0xff).toByte()
        header[6] = (chunkSize shr 16 and 0xff).toByte()
        header[7] = (chunkSize shr 24 and 0xff).toByte()
        // Format：WAVE
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()

        // The "fmt " subChunk describes the sound data's format:
        // SubChunk1ID：'fmt '
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        // SubChunk1Size：16 for PCM
        header[16] = subChunk1Size.toByte()
        header[17] = 0
        header[18] = 0
        header[19] = 0
        // AudioFormat：PCM = 1
        header[20] = audioFormat.toByte()
        header[21] = 0
        // NumChannels：Mono = 1，Stereo = 2
        header[22] = numChannels.toByte()
        header[23] = 0
        // SampleRate：采样率
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()
        // ByteRate：SampleRate * NumChannels * BitsPerSample / 8
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        // BlockAlign：NumChannels * BitsPerSample / 8
        header[32] = blockAlign.toByte()
        header[33] = 0
        // BitsPerSample：8 bits = 8, 16 bits = 16
        header[34] = bitsPerSample.toByte()
        header[35] = 0

        // The "data" subChunk contains the size of the data and the actual sound:
        // SubChunk2ID：data
        header[36] = 'd'.toByte() // data
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        // SubChunk2Size：NumSamples * NumChannels * BitsPerSample / 8
        header[40] = (subChunk2Size and 0xff).toByte()
        header[41] = (subChunk2Size shr 8 and 0xff).toByte()
        header[42] = (subChunk2Size shr 16 and 0xff).toByte()
        header[43] = (subChunk2Size shr 24 and 0xff).toByte()
        return header
    }

}