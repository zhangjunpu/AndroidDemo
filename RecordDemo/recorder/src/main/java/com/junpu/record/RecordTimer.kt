package com.junpu.record

import com.junpu.log.L
import com.junpu.log.logStackTrace
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * 录音定时器
 * @author junpu
 * @date 2019-10-19
 */
class RecordTimer {

    private var disposable: Disposable? = null
    private var amplitudeDisposable: Disposable? = null
    private var startTimeMillis = 0L

    /**
     * 开始定时器
     */
    fun start(callback: ((time: Long) -> Unit)? = null) {
        disposable = Observable.interval(0, 1L, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Long>() {
                override fun onStart() {
                    L.out("RecordTimer.onStart:")
                    startTimeMillis = System.currentTimeMillis()
                }

                override fun onNext(t: Long) {
                    L.out("RecordTimer.onNext: $t")
                    callback?.invoke(t)
                }

                override fun onComplete() {
                    L.out("RecordTimer.onComplete:")
                }

                override fun onError(e: Throwable) {
                    e.logStackTrace()
                }
            })
    }

    /**
     * 更新录音音量分贝线程
     */
    fun startAmplitude(callback: ((interval: Long) -> Unit)?) {
        amplitudeDisposable = Observable.interval(0, 100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback?.invoke(it)
            }
    }

    /**
     * 定时器停止
     */
    fun stop(callback: ((durationMillis: Long) -> Unit)? = null) {
        disposable?.dispose()
        callback?.invoke(System.currentTimeMillis() - startTimeMillis)
        startTimeMillis = 0L
        disposable = null
        amplitudeDisposable?.dispose()
        amplitudeDisposable = null
    }

    /**
     * 停止录音分贝动画线程
     */
    fun stopAmplitude() {
        amplitudeDisposable?.dispose()
        amplitudeDisposable = null
    }

    /**
     * 释放
     */
    fun release() {
        stop()
        disposable = null
    }
}