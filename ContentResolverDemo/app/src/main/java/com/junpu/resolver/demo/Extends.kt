package com.junpu.resolver.demo

import android.widget.TextView

/**
 *
 * @author junpu
 * @date 2019/3/6
 */

fun TextView.appendln(text: CharSequence?) {
    append(text)
    append("\n")
}

fun TextView.clear() {
    text = null
}