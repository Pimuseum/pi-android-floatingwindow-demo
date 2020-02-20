package com.pimuseum.demo.floatingwindow.core.inter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.pimuseum.demo.floatingwindow.core.FloatingWindowKit
import com.pimuseum.demo.floatingwindow.core.view.FloatingWindowLayout
import com.pimuseum.demo.floatingwindow.core.helper.ScreenUtils
import com.pimuseum.demo.floatingwindow.core.helper.TouchProxy
import java.lang.ref.WeakReference

abstract class AbstractFloatingWindow : FloatingWindowInterface, TouchProxy.OnTouchEventListener{

    /**
     * 手势代理
     */
    private val mTouchProxy by lazy { TouchProxy(this@AbstractFloatingWindow) }

    /**
     * weakActivity attach activity
     */
    var mAttachActivity: WeakReference<Activity>? = null

    /**
     * 内置悬浮窗 FrameLayout.LayoutParams
     */
    var floatingWindowParams: FrameLayout.LayoutParams? = null

    var mRootView: FrameLayout? = null
    private var mChildView: View? = null

    /**
     * 浮窗宽度
     */
    var windowWidth : Int = 0
    var windowHeight : Int = 0
    var windowMargin : Int = 0

    /******************************************      Abstract Common Logic      *********************************************/

    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    open fun attach(context: Context, activity: Activity) {

        mAttachActivity = WeakReference(activity)
        mRootView = FloatingWindowLayout(context)

        mRootView?.let {
            val rootViewLayoutParam = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            rootViewLayoutParam.gravity = Gravity.LEFT or Gravity.TOP

            mChildView = onCreateWindow(context, it)
            floatingWindowParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)

            //初始化Window 位置信息
            initWindowPosition()
            mChildView?.layoutParams = floatingWindowParams
            it.addView(mChildView)

            //设置根布局的手势拦截
            mChildView?.setOnTouchListener { v, event ->
                mTouchProxy.onTouchEvent(v, event)
            }

            onWindowCreated(it)

            val mDecorView = activity.window.decorView as FrameLayout
                mDecorView.postDelayed({
                    mDecorView.addView(it)
                    FloatingWindowKit.floatingWindows[activity.hashCode()] = this@AbstractFloatingWindow
                    onResume(mDecorView)
                },300)
        }
    }

    open fun detach() {
        mAttachActivity?.get()?.let { activity->

            val mDecorView = activity.window.decorView as FrameLayout
            if (mRootView != null && mDecorView.indexOfChild(mRootView as View) != -1) {
                mDecorView.postDelayed({
                    mDecorView.removeView(mRootView)
                    FloatingWindowKit.floatingWindows.remove(activity.hashCode())
                    onDestroy(mDecorView)
                },300)
            }
        }
    }

    override fun onResume(mDecorView : FrameLayout?) {

    }

    override fun onDestroy(mDecorView : FrameLayout?) {

    }

    /*****************************************         Touch Event         *********************************************/

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
        floatingWindowParams?.let {
            it.leftMargin += dx
        }
        floatingWindowParams?.let {
            it.topMargin += dy
        }

        //更新浮窗位置
        floatingWindowParams?.let { resetBorderline(it) }
        mChildView?.layoutParams = floatingWindowParams
    }

    override fun onUp(x: Int, y: Int) {
        adjustWindowPosition()
    }

    override fun onDown(x: Int, y: Int) {

    }

    override fun onAreaClick(xInParent: Int, yInParent: Int) {

    }

    /*****************************************         Window Screen Logic         *********************************************/

    open fun adjustWindowPosition() {
        if (isPortrait()) {
            floatingWindowParams?.let { param ->
                if (param.leftMargin <= (getAppScreenWidth() -  windowWidth)/2) {
                    param.leftMargin = windowMargin
                } else {
                    param.leftMargin = getAppScreenWidth() -  windowWidth - windowMargin
                }
            }
        } else {
            floatingWindowParams?.let { param ->
                if (param.leftMargin <= (getAppScreenHeight() -  windowWidth)/2) {
                    param.leftMargin = windowMargin
                } else {
                    param.leftMargin = getAppScreenHeight() -  windowWidth - windowMargin
                }
            }
        }

        //更新浮窗位置,记录位置
        floatingWindowParams?.let { param->
            resetBorderline(param)
            FloatingWindowKit.saveWindowPosition(param.leftMargin,param.topMargin)
        }
    }

    private fun initWindowPosition() {
        if (FloatingWindowKit.obtainWindowMarginLeft() > 0) {
            floatingWindowParams?.leftMargin = FloatingWindowKit.obtainWindowMarginLeft()
        } else {
            floatingWindowParams?.leftMargin = windowMargin
        }


        if(FloatingWindowKit.obtainWindowMarginTop() > 0) {
            floatingWindowParams?.topMargin = FloatingWindowKit.obtainWindowMarginTop()
        } else {
            floatingWindowParams?.topMargin = getAppScreenHeight() - windowHeight - ScreenUtils.dp2px(mAttachActivity?.get(),60F)
        }
    }

    /**
     * 限制边界 调用的时候必须保证是在控件能获取到宽高德前提下
     */
    private fun resetBorderline(frameLayoutParams: FrameLayout.LayoutParams) {

        if (frameLayoutParams.topMargin <= ScreenUtils.dp2px(mAttachActivity?.get(),60F)) {
            frameLayoutParams.topMargin = ScreenUtils.dp2px(mAttachActivity?.get(),60F)
        }

        if (isPortrait()) {
            if (frameLayoutParams.topMargin >= getAppScreenHeight() - windowHeight - ScreenUtils.dp2px(mAttachActivity?.get(),60F)) {
                frameLayoutParams.topMargin = getAppScreenHeight() - windowHeight - ScreenUtils.dp2px(mAttachActivity?.get(),60F)
            }
        } else {
            if (frameLayoutParams.topMargin >= getAppScreenWidth() - windowHeight - ScreenUtils.dp2px(mAttachActivity?.get(),60F)) {
                frameLayoutParams.topMargin = getAppScreenWidth() - windowHeight - ScreenUtils.dp2px(mAttachActivity?.get(),60F)
            }
        }


        if (frameLayoutParams.leftMargin <= windowMargin) {
            frameLayoutParams.leftMargin = windowMargin
        }

        if (isPortrait()) {
            if (frameLayoutParams.leftMargin >= getAppScreenWidth() - windowWidth - windowMargin) {
                frameLayoutParams.leftMargin = getAppScreenWidth() - windowWidth - windowMargin
            }
        } else {
            if (frameLayoutParams.leftMargin >= getAppScreenHeight() - windowWidth - windowMargin) {
                frameLayoutParams.leftMargin = getAppScreenHeight() - windowWidth - windowMargin
            }
        }
    }

    /**
     * 是否是竖屏
     */
    private fun isPortrait() : Boolean {
        return FloatingWindowKit.Application.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    /**
     * 获取屏幕宽度
     */
    private fun getAppScreenWidth(): Int {
        val wm = FloatingWindowKit.Application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.x
    }

    /**
     * 获取屏幕高度
     */
    private fun getAppScreenHeight(): Int {
        val wm = FloatingWindowKit.Application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.y
    }
}