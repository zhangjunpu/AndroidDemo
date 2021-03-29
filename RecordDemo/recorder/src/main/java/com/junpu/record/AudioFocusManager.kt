package com.junpu.record

import android.content.Context
import android.media.AudioManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat

/**
 * 音频焦点管理
 * @author junpu
 * @date 2019-10-22
 */
class AudioFocusManager(context: Context) {

    private val audioManager by lazy {
        context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    private var audioFocusRequest: AudioFocusRequestCompat? = null

    /**
     * 获取音频焦点
     */
    fun requestFocus(focusChangeListener: (Int) -> Unit) {
        audioFocusRequest = getAudioFocusRequest(focusChangeListener).also {
            AudioManagerCompat.requestAudioFocus(audioManager, it)
        }
    }

    /**
     * 释放焦点
     */
    fun releaseFocus() {
        audioFocusRequest?.let { AudioManagerCompat.abandonAudioFocusRequest(audioManager, it) }
    }

    private fun getAudioFocusRequest(focusChangeListener: (focusChange: Int) -> Unit): AudioFocusRequestCompat {
        val audioAttributes = AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_VOICE_COMMUNICATION) // USAGE_NOTIFICATION_COMMUNICATION_INSTANT
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_SPEECH)
            .build()
        return AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener(focusChangeListener)
            .build()
    }
}