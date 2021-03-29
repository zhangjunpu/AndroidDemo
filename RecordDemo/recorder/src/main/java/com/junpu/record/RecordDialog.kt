package com.junpu.record

import android.content.Context
import android.content.DialogInterface
import com.junpu.utils.setVisibility
import com.junpu.widget.dialog.BaseDialog
import kotlinx.android.synthetic.main.dialog_record.*

/**
 * 录音Dialog
 * @author junpu
 * @date 2019-10-18
 */
class RecordDialog : BaseDialog {

    constructor(context: Context) : super(context, R.style.Theme_Dialog_Transparent)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    override fun layout() = R.layout.dialog_record

    override fun initView() {
        setCanceledOnTouchOutside(false)
    }

    /**
     * 设置声波等级
     */
    fun setVoiceLevel(level: Int) {
        imageLevel?.setImageLevel(level)
    }

    /**
     * 设置UI状态，倒计时状态，声波状态
     */
    fun setTimerStatus(isTimerStatus: Boolean) {
        groupVoice?.setVisibility(!isTimerStatus)
        textTime?.setVisibility(isTimerStatus)
    }

    /**
     * 更新录音倒计时
     */
    fun uploadTime(timeSecond: Long) {
        textTime?.text = timeSecond.toString()
    }

    /**
     * 状态变更
     */
    fun setStatus(status: Int) {
        when (status) {
            // 默认状态
            STATUS_NONE -> textTips?.setText(R.string.touch_talk)
            // 录音准备
            STATUS_RECORD_PREPARE -> textTips?.setText(R.string.touch_talk)
            // 开始录音
            STATUS_RECORD_START -> textTips?.setText(R.string.slide_up_cancel)
            // 录音中
            STATUS_RECORDING -> textTips?.setText(R.string.slide_up_cancel)
            // 录音取消意图
            STATUS_RECORD_WANT_CANCEL -> textTips?.setText(R.string.loosen_cancel)
            // 录音取消
            STATUS_RECORD_CANCEL -> textTips?.setText(R.string.touch_talk)
            // 录音完成
            STATUS_RECORD_COMPLETE -> textTips?.setText(R.string.long_touch_rerecord)
        }
    }
}