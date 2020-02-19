package com.pimuseum.demo.floatingwindow.core.inter

import android.content.Context
import android.view.View
import android.widget.FrameLayout


interface FloatingWindowInterface {

    fun onCreateWindow(context: Context, rootView: FrameLayout) : View

    fun onWindowCreated(rootView: FrameLayout)

    fun onResume(mDecorView : FrameLayout? = null)

    fun onDestroy(mDecorView : FrameLayout? = null)
}