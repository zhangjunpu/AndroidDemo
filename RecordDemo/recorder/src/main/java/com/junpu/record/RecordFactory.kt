package com.junpu.record

import com.junpu.record.manager.AudioRecordManager
import com.junpu.record.manager.IRecordManager
import com.junpu.record.manager.MediaRecordManager
import com.junpu.record.player.AudioTrackPlayer
import com.junpu.record.player.IRecordPlayer
import com.junpu.record.player.MediaRecordPlayer

/**
 * 录音工厂类
 * @author junpu
 * @date 2019-10-31
 */
interface RecordFactory {
    fun createRecordManager(recordType: Int): IRecordManager?
    fun createRecordPlayer(recordPlayerType: Int): IRecordPlayer?
}

/**
 * 录音工厂实现类
 * @author junpu
 * @date 2019-10-31
 */
class RecordFactoryImpl : RecordFactory {
    override fun createRecordManager(recordType: Int): IRecordManager? {
        return when (recordType) {
            RECORD_TYPE_MEDIA -> MediaRecordManager()
            RECORD_TYPE_AUDIO -> AudioRecordManager()
            else -> null
        }
    }

    override fun createRecordPlayer(recordPlayerType: Int): IRecordPlayer? {
        return when (recordPlayerType) {
            RECORD_TYPE_MEDIA -> MediaRecordPlayer()
            RECORD_TYPE_AUDIO -> AudioTrackPlayer()
            else -> null
        }
    }
}