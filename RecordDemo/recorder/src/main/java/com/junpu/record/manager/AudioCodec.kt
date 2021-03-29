package com.junpu.record.manager

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import com.junpu.log.L
import com.junpu.log.logStackTrace
import com.junpu.record.CODEC_TIMEOUT
import java.io.DataOutputStream

/**
 * audio音频转码
 * @author junpu
 * @date 2019-11-05
 */
class AudioCodec(private val config: AudioRecordConfig) {

    private var mediaCodec: MediaCodec? = null
    private var mediaFormat: MediaFormat? = null
    private var bufferInfo: MediaCodec.BufferInfo? = null

    private val mimeType = MediaFormat.MIMETYPE_AUDIO_AAC

    init {
        initMediaCodec()
    }

    private fun initMediaCodec() {
        try {
            bufferInfo = MediaCodec.BufferInfo()
            mediaFormat =
                MediaFormat.createAudioFormat(mimeType, config.sampleRateInHz, config.numChannels)
                    .apply {
                        setInteger(
                            MediaFormat.KEY_AAC_PROFILE,
                            MediaCodecInfo.CodecProfileLevel.AACObjectLC
                        )
                        setInteger(MediaFormat.KEY_BIT_RATE, config.bitRate)
                        setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024)
                    }
//            mediaCodec = MediaCodec.createEncoderByType(mimeType).apply {
//                configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
//            }
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    fun encodeAsync(buffer: Short, fos: DataOutputStream?) {
        L.out("AudioCodec.prepare -> Thread.currentThread().name: ${Thread.currentThread().name}")
        mediaCodec = MediaCodec.createEncoderByType(mimeType).apply {
            setCallback(object : MediaCodec.Callback() {
                override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                    L.out("AudioCodec.onInputBufferAvailable -> index: ${index}")
                    codec.getInputBuffer(index)?.run {
                        clear()
//                        buffer.forEach { putShort(it) }
                        putShort(buffer)
                    }
                    codec.queueInputBuffer(index, 0, 2, System.nanoTime(), 0)
                }

                override fun onOutputBufferAvailable(
                    codec: MediaCodec,
                    index: Int,
                    info: MediaCodec.BufferInfo
                ) {
                    L.out("AudioCodec.onOutputBufferAvailable -> index: ${index}")
                    val outputBuffer = codec.getOutputBuffer(index) ?: return
//                    val length = info.size + 7
//                    val bytes = ByteArray(length)
//                    addAdtsToPacket(bytes, length)
//                    outputBuffer.get(bytes, 7, info.size)
//                    fos?.write(bytes, 0, bytes.size)
                    fos?.writeShort(outputBuffer.short.toInt())
                    codec.releaseOutputBuffer(index, false)
                }

                override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                    L.out("AudioCodec.onOutputFormatChanged -> codec: ${codec}")
                }

                override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                    e.logStackTrace()
                }
            })
            configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        }
        mediaCodec?.start()
    }

    fun encode(buffer: ShortArray, fos: DataOutputStream?) {
        if (mediaCodec == null) initMediaCodec()
        val codec = mediaCodec!!
        val inputBufferIndex = codec.dequeueInputBuffer(CODEC_TIMEOUT)
        if (inputBufferIndex >= 0) {
            codec.getInputBuffer(inputBufferIndex)?.apply {
                clear()
                buffer.forEach { putShort(it) }
                limit(buffer.size)
            }
            codec.queueInputBuffer(inputBufferIndex, 0, 2, System.nanoTime(), 0)
        }
        var bytes: ByteArray? = null
        var outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo!!, CODEC_TIMEOUT)
        while (outputBufferIndex >= 0) {
            val outputBuffer = codec.getOutputBuffer(outputBufferIndex) ?: return
            val length = bufferInfo!!.size + 7
            if (bytes == null) bytes = ByteArray(length)
            addAdtsToPacket(bytes, length)
            outputBuffer.get(bytes, 7, bufferInfo!!.size)
            fos?.write(bytes, 0, bytes.size)
//            fos?.writeShort(outputBuffer.short.toInt())
            codec.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo!!, CODEC_TIMEOUT)
        }
    }

    fun encode(buffer: ByteArray, fos: DataOutputStream?) {
        if (mediaCodec == null) initMediaCodec()
        val codec = mediaCodec!!
        val inputBufferIndex = codec.dequeueInputBuffer(CODEC_TIMEOUT)
        if (inputBufferIndex >= 0) {
            codec.getInputBuffer(inputBufferIndex)?.apply {
                clear()
                put(buffer)
                limit(buffer.size)
            }
            codec.queueInputBuffer(inputBufferIndex, 0, 2, System.nanoTime(), 0)
        }
        var bytes: ByteArray? = null
        var outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo!!, CODEC_TIMEOUT)
        while (outputBufferIndex >= 0) {
            val outputBuffer = codec.getOutputBuffer(outputBufferIndex) ?: return
            val length = bufferInfo!!.size + 7
            if (bytes == null) bytes = ByteArray(length)
            addAdtsToPacket(bytes, length)
            outputBuffer.get(bytes, 7, bufferInfo!!.size)
            fos?.write(bytes, 0, bytes.size)
            codec.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo!!, CODEC_TIMEOUT)
        }
    }

    /**
     * 给编码出的aac裸流添加adts头字段
     * @param packet 要空出前7个字节，否则会搞乱数据
     * @param packetLen 7
     */
    private fun addAdtsToPacket(packet: ByteArray, packetLen: Int) {
        val profile = 2  // AAC LC
        val freqIdx = 4  // 44.1KHz
        val chanCfg = 2  // CPE
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }

    fun stop() {
        try {
            mediaCodec?.stop()
//            mediaCodec?.release()
        } catch (e: Exception) {
            e.logStackTrace()
        }
    }

    fun release() {
        mediaCodec?.release()
        mediaCodec = null
    }
}