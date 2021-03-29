package com.junpu.topdf.utils

import android.animation.Animator
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation
import android.widget.SeekBar

/**
 * Animation监听
 * @author junpu
 * @date 2019-10-08
 */
open class SimpleAnimationListener : Animation.AnimationListener {
    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
    }
}

/**
 * ObjectAnimator 监听
 * @author junpu
 * @date 2019-08-14
 */
open class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }
}

/**
 * SeekBar进度条监听
 * @author junpu
 * @date 2019-08-06
 */

open class SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }
}

/**
 * EditText文字变价监听
 * @author junpu
 * @date 2019-07-21
 */
open class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}