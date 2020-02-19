package com.pimuseum.demo.floatingwindow.core.helper

import android.view.View
import android.view.ViewGroup

fun View.noFastClick(delay : Long? = null) {
    isEnabled = false
    postDelayed({isEnabled = true},delay?:1000)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.show(show: Boolean) {
    if (show) visible() else gone()
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.isGone(): Boolean = visibility == View.GONE


fun View.setMargins (l : Int = 0, t : Int = 0,r : Int = 0, b : Int = 0) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val p =  this.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(l, t, r, b)
        this.requestLayout()
    }
}

