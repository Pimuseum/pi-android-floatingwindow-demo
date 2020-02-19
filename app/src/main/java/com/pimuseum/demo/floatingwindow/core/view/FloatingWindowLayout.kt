package com.pimuseum.demo.floatingwindow.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class FloatingWindowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onTouchInDecorViewListener : OnTouchInDecorView? = null

    fun setOnTouchInDecorViewListener(onTouchInDecorViewListener : OnTouchInDecorView) {
        this.onTouchInDecorViewListener = onTouchInDecorViewListener
    }

    fun removeOnTouchInDecorViewListener() {
        this.onTouchInDecorViewListener = null
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            onTouchInDecorViewListener?.onTouch(ev.rawX.toInt(),ev.rawY.toInt())
        }
        return super.dispatchTouchEvent(ev)
    }

    interface OnTouchInDecorView {
        fun onTouch(rawX : Int , rawY : Int)
    }
}