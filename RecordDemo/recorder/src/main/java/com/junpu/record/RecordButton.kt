package com.junpu.record

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.junpu.log.L
import com.junpu.record.manager.IRecordManager
import com.junpu.record.player.IRecordPlayer
import com.junpu.record.utils.AudioUtils
import com.junpu.toast.toast
import com.junpu.utils.*
import com.junpu.widget.dialog.ToastDialog
import kotlinx.android.synthetic.main.record_button.view.*
import kotlin.math.abs
import kotlin.math.min

/**
 * 语音录制按钮
 * @author junpu
 * @date 2019-10-18
 */
class RecordButton : FrameLayout, View.OnTouchListener {

    companion object {
        private const val DEFAULT_MIN_MOVE_DIST = 2 // 最小滑动2dp出发事件
        private const val DEFAULT_MAX_MOVE_DIST = 7f // 最大滑动距离屏幕高度1/7

        private const val MAX_LONG_CLICK_MOVE_DIST = 10 // 长按移动距离限制 10dp
        private const val LONG_CLICK_DAILY = 500L // 长按触发时间 500ms
    }

    private var recordFactory: RecordFactory? = null
    private var recordManager: IRecordManager? = null // 录音管理
    private var recordPlayer: IRecordPlayer? = null // 播放器
    private var recordDialog: RecordDialog? = null // Record弹窗
    private var recordTimer: RecordTimer? = null // 录音定时器
    private var audioFocusManager: AudioFocusManager? = null // 焦点管理

    private var downX = 0f
    private var downY = 0f
    private var lastY = 0f
    private var minMoveDist = 0f
    private var maxMoveDist = 0f
    private var isRecording = false // 是否正在录音
    private var isCancel = false // 是否取消状态
    private var isComplete = false // 是否为完成状态
    private var isClickChecking = false // 单击检查
    private var isLongClickChecking = false // 长按检查

    private var onRecordListener: ((action: Int, path: String?, duration: Long) -> Unit)? =
        null // 录音状态监听
    private var audioUrl: String? = null
    private var audioDuration: Long = 0

    /**
     * 是否可以录音
     */
    var isRecordEnable = true

    /**
     * 是否可以长按重录
     */
    var isLongClickEnable = true

    /**
     * 是否允许detachedToWindow的时候释放，防止按钮在RecyclerView中划出屏幕被释放掉
     */
    var isDetachedRelease = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        context.inflate(R.layout.record_button, this, true)
        minMoveDist = dip(DEFAULT_MIN_MOVE_DIST).toFloat()
        maxMoveDist = screenHeight / DEFAULT_MAX_MOVE_DIST
        btnRecord?.setOnTouchListener(this)
        btnCancel?.setOnClickListener {
            ToastDialog.Builder(context)
                .setMessage(R.string.confirm_delete_record)
                .setPositiveButton { _, _ -> changeStatus(STATUS_NONE) }
                .show()
        }
        recordFactory = RecordFactoryImpl()
        recordManager = recordFactory?.createRecordManager(RECORD_TYPE_MEDIA)
        recordPlayer = recordFactory?.createRecordPlayer(RECORD_TYPE_MEDIA)
        recordDialog = RecordDialog(context)
        recordTimer = RecordTimer()
        audioFocusManager = AudioFocusManager(context)
    }

    /**
     * 音频焦点回调
     */
    private val audioFocusChangeListener: (Int) -> Unit = {
        L.out("RecordButton.audioFocusChangeListener -> it: ${it}")
        when (it) {
            AudioManager.AUDIOFOCUS_GAIN -> Unit
            AudioManager.AUDIOFOCUS_LOSS -> if (recordPlayer?.isPlaying == true) stopRecordPlay()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> Unit
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> Unit
        }
    }

    /**
     * audio录制线程
     */
    private val startCallback = Runnable {
        changeStatus(STATUS_RECORD_START)
    }

    /**
     * 长按事件
     */
    private val checkForLongClick = Runnable {
        if (isLongClickChecking) {
            isClickChecking = false
            isLongClickChecking = false
            isComplete = false
            performLongClickForComplete()
        }
    }

    /**
     * 事件监听
     */
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (!isRecordEnable) {
            if (event?.action == MotionEvent.ACTION_UP) {
                onRecordListener?.invoke(RECORD_ACTION_DISABLE, null, 0)
            }
            return true
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                downY = event.rawY
                lastY = event.rawY
                if (!isComplete) {
                    changeStatus(STATUS_RECORD_PREPARE)
                } else {
                    isClickChecking = true
                    if (isLongClickEnable) {
                        isLongClickChecking = true
                        postDelayed(checkForLongClick, LONG_CLICK_DAILY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.rawY
                if (!isComplete && abs(y - lastY) > minMoveDist) {
                    isCancel = if (abs(y - downY) > maxMoveDist) {
                        changeStatus(STATUS_RECORD_WANT_CANCEL)
                        true
                    } else {
                        changeStatus(STATUS_RECORDING)
                        false
                    }
                    lastY = event.rawY
                } else if (isComplete && isLongClickChecking) {
                    if (!checkForLongClick(v, event.x, event.y, event.rawX, y)) {
                        removeCallbacks(checkForLongClick)
                        isLongClickChecking = false
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isComplete) {
                    if (isRecording) {
                        if (isCancel) {
                            changeStatus(STATUS_RECORD_CANCEL)
                        } else {
                            changeStatus(STATUS_RECORD_COMPLETE)
                        }
                        isRecording = false
                    } else {
                        removeCallbacks(startCallback)
                    }
                } else {
                    if (checkForClick(v, event.x, event.y) && isClickChecking) {
                        if (isLongClickEnable) {
                            removeCallbacks(checkForLongClick)
                            isLongClickChecking = false
                        }
                        isClickChecking = false
                        performClickForComplete()
                    }
                }
                isCancel = false
            }
        }
        return true
    }

    /**
     * 检测点击是否超出边界
     */
    private fun checkForClick(v: View?, x: Float, y: Float): Boolean {
        val width = v?.width ?: 0
        val height = v?.height ?: 0
        if (x >= 0 && x <= width && y >= 0 && y <= height) {
            return true
        }
        return false
    }

    /**
     * 检测长按是否符合触发条件
     */
    private fun checkForLongClick(v: View?, x: Float, y: Float, rawX: Float, rawY: Float): Boolean {
        val moveDistinct = dip(MAX_LONG_CLICK_MOVE_DIST)
        if (!checkForClick(
                v,
                x,
                y
            ) || abs(rawX - downX) > moveDistinct || abs(rawY - downY) > moveDistinct
        ) {
            return false
        }
        return true
    }

    /**
     * 执行点击事件
     */
    private fun performClickForComplete() {
        val path = recordManager?.getAudioPath()
        if (path.isNullOrBlank() && audioUrl.isNullOrBlank()) return
        val playUrl = path ?: audioUrl
        playAudio(playUrl)
    }

    /**
     * 执行长按事件
     */
    private fun performLongClickForComplete() {
        L.out("RecordButton.performLongClickForComplete: long click perform")
        changeStatus(STATUS_NONE)
        changeStatus(STATUS_RECORD_START)
    }

    /**
     * 状态变化
     */
    private fun changeStatus(status: Int) {
        recordDialog?.setStatus(status)
        when (status) {
            // 默认状态
            STATUS_NONE -> {
                audioDuration = 0
                setButtonMode(true)
                stopRecordPlay()
                recordManager?.cancel()
                onRecordListener?.invoke(RECORD_ACTION_NONE, null, 0)
            }
            // 录音准备
            STATUS_RECORD_PREPARE -> {
                textTips?.textResource = R.string.touch_talk
                postDelayed(startCallback, 200)
            }
            // 开始录音
            STATUS_RECORD_START -> {
                onRecordListener?.invoke(RECORD_ACTION_START, null, 0)
                textTips?.textResource = R.string.slide_up_cancel
                recordDialog?.show()
                recordDialog?.setTimerStatus(false)
                isRecording = true
                context.vibrateOneShot() // 震动
                recordManager?.start {
                    recordTimer?.start {
                        // 最后10秒开始倒计时
                        val countDownTime = (MAX_RECORD_DURATION - RECORD_COUNTDOWN_NODE).toLong()
                        if (it >= countDownTime) {
                            if (it == countDownTime) {
                                recordDialog?.setTimerStatus(true)
                                recordTimer?.stopAmplitude()
                            }
                            recordDialog?.uploadTime(MAX_RECORD_DURATION - it)
                            if (it > MAX_RECORD_DURATION) changeStatus(STATUS_RECORD_COMPLETE)
                        }
                    }
                    recordTimer?.startAmplitude {
                        val level = recordManager?.getMicAmplitude() ?: 0
                        recordDialog?.setVoiceLevel(level)
                    }
                }
                audioFocusManager?.requestFocus(audioFocusChangeListener)
            }
            // 录音中
            STATUS_RECORDING -> textTips?.textResource = R.string.slide_up_cancel
            // 录音取消意图
            STATUS_RECORD_WANT_CANCEL -> textTips?.textResource = R.string.loosen_cancel
            // 录音取消
            STATUS_RECORD_CANCEL -> {
                textTips?.textResource = R.string.touch_talk
                recordDialog?.dismiss()
                recordManager?.cancel()
                recordTimer?.stop()
                audioFocusManager?.releaseFocus()
                onRecordListener?.invoke(RECORD_ACTION_CANCEL, null, 0)
            }
            // 录音完成
            STATUS_RECORD_COMPLETE -> {
                recordDialog?.dismiss()
                var audioPath: String? = null
                recordManager?.stop { duration, path ->
                    if (duration > 0) audioDuration = duration
                    audioPath = path
                    L.out(
                        "RecordButton.changeStatus -> duration: ${duration}, ${
                            formatVoiceDuration(
                                duration
                            )
                        }"
                    )
                }
                recordTimer?.stop {
                    if (audioDuration <= 0) audioDuration = min(it, 60 * 1000)
                }
                if (audioDuration < MIN_RECORD_DURATION * 1000) {
                    toast(R.string.audio_comment_length_tips)
                    setButtonMode(true)
                    audioDuration = 0
                    onRecordListener?.invoke(RECORD_ACTION_CANCEL, null, 0)
                } else {
                    setButtonMode(
                        isRecordMode = false,
                        isCancelable = true,
                        duration = audioDuration
                    )
                    onRecordListener?.invoke(RECORD_ACTION_COMPLETE, audioPath, audioDuration)
                }
                isLongClickEnable = true // 录音完成后可以删除、重录
                audioFocusManager?.releaseFocus()
            }
        }
    }

    /**
     * 设置按钮模式
     * @param isRecordMode true录音模式，false播放模式
     * @param isCancelable 是否可以删除
     * @param duration 录音时长
     */
    private fun setButtonMode(
        isRecordMode: Boolean,
        isCancelable: Boolean = true,
        duration: Long = 0
    ) {
        if (isRecordMode) {
            textTips?.textResource = R.string.touch_talk
            isComplete = false
            groupVoice?.gone()
            btnCancel.gone()
            imageMicrophone?.visible()
        } else {
            textTips?.textResource = R.string.long_touch_rerecord
            isComplete = true
            groupVoice?.visible()
            if (isCancelable) btnCancel.visible()
            imageMicrophone?.gone()
            updateTimeSeconds(duration)
        }
    }

    /**
     * 设置语音时间
     */
    private fun updateTimeSeconds(duration: Long) {
        if (duration >= 0) textSecond?.text = formatVoiceDuration(duration)
    }

    /**
     * 播放音频
     */
    private fun playAudio(audioPath: String?) {
        L.out("RecordButton.playAudio:")
        if (recordPlayer?.isPlaying == true) {
            stopRecordPlay()
        } else {
            audioPath?.let {
                recordPlayer?.play(it) {
                    startVoiceAnimation(false)
                    audioFocusManager?.releaseFocus()
                }
                startVoiceAnimation(true)
                audioFocusManager?.requestFocus(audioFocusChangeListener)
            }
        }
    }

    /**
     * 停止播放录音
     */
    private fun stopRecordPlay() {
        recordPlayer?.stop()
        startVoiceAnimation(false)
        audioFocusManager?.releaseFocus()
    }

    /**
     * 开启、关闭voice播放动画
     */
    private fun startVoiceAnimation(flag: Boolean) {
        (imageVoice.drawable as? AnimationDrawable)?.run {
            if (flag) {
                start()
            } else {
                stop()
                selectDrawable(0)
            }
        }
    }

    fun onPause() {
        // 如果播放中则暂停
        if (recordPlayer?.isPlaying == true) {
            stopRecordPlay()
        }
    }

    /**
     * RecyclerView Item 划出屏幕会调用
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isDetachedRelease) release()
    }

    /**
     * 释放
     */
    fun release() {
        stopRecordPlay()
        recordManager?.release()
        recordTimer?.release()
        recordPlayer?.release()
    }

    /**
     * 设置录音完成监听
     */
    fun setOnRecordListener(listener: ((action: Int, path: String?, duration: Long) -> Unit)?) {
        this.onRecordListener = listener
    }

    /**
     * 录音是否是完成状态
     */
    fun isRecordComplete(): Boolean = isComplete

    /**
     * 获取录音文件路径
     */
    fun getRecordPath(): String? = recordManager?.getAudioPath()

    /**
     * 获取音频时长
     */
    fun getRecordDuration(): Long = audioDuration

    /**
     * 设置Url音频链接
     */
    fun setUrl(url: String?) {
        changeStatus(STATUS_NONE)
        audioUrl = url
        if (url.isNullOrBlank()) return
        var duration = -1L
        AudioUtils.getDurationAsync(url) {
            L.out("RecordButton.setUrl -> duration: $it")
            duration = it
            updateTimeSeconds(duration)
        }
        setButtonMode(isRecordMode = false, isCancelable = false, duration = duration)
        isComplete = true
        recordManager?.clearAudioPath()
    }

    /**
     * 设置是否为最终完成状态，只能播放，不能重录
     * @param flag true，禁止长按、禁止删除当前录音，false：可以长按
     */
    fun setCompleteFinal(flag: Boolean) {
        isLongClickEnable = !flag
        when {
            flag -> {
                btnCancel?.gone()
                textTips?.textResource = R.string.click_to_play
            }
            isComplete -> {
                btnCancel?.visible()
                textTips?.textResource = R.string.long_touch_rerecord
            }
            else -> textTips?.textResource = R.string.touch_talk
        }
    }

    /**
     * 震动：一次短暂震动
     */
    private fun Context.vibrateOneShot(duration: Long = 50) {
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(duration)
            }
        }
    }
}