package com.junpu.aidl.client.demo

import android.widget.TextView

/**
 *
 * @author junpu
 * @date 2019/3/9
 */

fun TextView.appendln(text: CharSequence?) {
    append(text ?: "")
    append("\n")
}