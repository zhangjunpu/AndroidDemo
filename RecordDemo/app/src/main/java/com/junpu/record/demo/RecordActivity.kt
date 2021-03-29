package com.junpu.record.demo

import android.Manifest
import android.os.Bundle
import com.junpu.gopermissions.PermissionsActivity
import com.junpu.log.L
import com.junpu.record.*
import com.junpu.record.manager.AudioCodec
import com.junpu.record.manager.AudioRecordConfig
import kotlinx.android.synthetic.main.activity_recode.*
import java.io.File

/**
 * 录音Activity
 * @author junpu
 * @date 2019-10-23
 */
class RecordActivity : PermissionsActivity() {

    //    private val soundUrl = "https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg"
    private val soundUrl = "http://music.163.com/song/media/outer/url?id=554241732"
//    private val soundUrl = Environment.getExternalStorageDirectory().absolutePath + File.separator + "Download/my name.mp3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recode)
        var recordPath: String? = null
        btnRecord?.setOnRecordListener { action, path, duration ->
            when (action) {
                RECORD_ACTION_START -> textInfo?.append("录音开始...\n")
                RECORD_ACTION_CANCEL -> textInfo?.append("录音取消...\n")
                RECORD_ACTION_COMPLETE -> {
                    recordPath = path
                    textInfo?.append("录音完成：$path \n")
                }
                RECORD_ACTION_NONE -> textInfo?.append("录音重置 \n")
                RECORD_ACTION_DISABLE -> textInfo?.append("录音不可用 \n")
            }
        }
        btnSetUrl?.setOnClickListener {
            btnRecord.setUrl(soundUrl)
        }
        var flag = true
        btnDisable?.setOnClickListener {
            btnRecord?.setCompleteFinal(flag)
            flag = !flag
        }
        val recordFactory = RecordFactoryImpl()
        val recordPlayer = recordFactory.createRecordPlayer(RECORD_TYPE_AUDIO)

        val pcmPath =
            RecordCache.audioCachePath + File.separator + "audio" + AudioRecordConfig.RecordFormat.PCM.format
        val wavPath =
            RecordCache.audioCachePath + File.separator + "audio" + AudioRecordConfig.RecordFormat.WAV.format
        val m4aPath =
            RecordCache.audioCachePath + File.separator + "audio" + AudioRecordConfig.RecordFormat.M4A.format
        btnPlayPcm?.setOnClickListener {
            if (recordPlayer?.isPlaying == true) {
                recordPlayer.stop()
            } else {
                recordPlayer?.play(pcmPath) {
                    L.vv("RecordActivity.onCreate -> pcm play end")
                }
            }
        }
        btnPlayPcmGain?.setOnClickListener {
            if (recordPlayer?.isPlaying == true) {
                recordPlayer.stop()
            } else {
                recordPlayer?.play(wavPath) {
                    L.vv("RecordActivity.onCreate -> pcm play end")
                }
            }
        }
        btnPlayM4a?.setOnClickListener {
            if (recordPlayer?.isPlaying == true) {
                recordPlayer.stop()
            } else {
                recordPlayer?.play(m4aPath) {
                    L.vv("RecordActivity.onCreate -> aac play end")
                }
            }
        }

        val codecPath = RecordCache.audioCachePath + File.separator + "audio1.m4a"
        audioCodec = AudioCodec(AudioRecordConfig())

        btnCodec?.setOnClickListener {
//            val file = File(pcmPath)
//            val fileOut = File(codecPath)
//            val dis = DataInputStream(file.inputStream())
//            val dos = DataOutputStream(fileOut.outputStream())
//            audioCodec?.initEncodeAsync(dos)
//
//            val bytes = ByteArray(1024)
//            while (dis.available() > 0) {
//                val read = dis.read(bytes)
//                L.vv("RecordActivity.onCreate -> read: ${read}")
//                if (read > 0) {
//                    audioCodec?.encodeAsync(bytes)
//                }
//            }
        }
        btnCodecPlay?.setOnClickListener {
            if (recordPlayer?.isPlaying == true) {
                recordPlayer.stop()
            } else {
                recordPlayer?.play(codecPath) {
                    L.vv("RecordActivity.onCreate -> aac play end")
                }
            }
        }
    }

    private var audioCodec: AudioCodec? = null

    override fun onResume() {
        super.onResume()
        checkPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.VIBRATE
            )
        )
    }

}
