package com.pimuseum.demo.floatingwindow.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.pimuseum.demo.floatingwindow.core.inter.AbstractFloatingWindow
import com.pimuseum.demo.floatingwindow.core.info.FloatingWindowInfo
import com.pimuseum.demo.floatingwindow.core.windows.RadioFloatingWindow
import com.pimuseum.demo.floatingwindow.activity.RadioActivity

/**
 * 内置悬浮窗Kit
 */
object FloatingWindowKit {

    private val TAG = "FloatingWindowKit"

    /**
     * 添加到每个Activity中的内置悬浮窗集合
     */
    var floatingWindows : HashMap<Int, AbstractFloatingWindow> = hashMapOf()
    lateinit var Application : Application

    /**
     * 浮窗的 ui 状态
     */
    private var windowInfo : FloatingWindowInfo = FloatingWindowInfo::class.java.newInstance()

    var openRadio : Boolean = false

    fun install(application: Application) {
        Application = application
        Application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{

            /**
             * detach 悬浮窗
             */
            override fun onActivityPaused(activity: Activity?) {
                //移除 window
                if (floatingWindows[activity.hashCode()] != null) {
                    activity?.let { attachActivity ->
                        floatingWindows[attachActivity.hashCode()]?.detach()
                    }
                }
            }

            /**
             * 根据电台音频信息 attach 悬浮窗
             */
            override fun onActivityResumed(activity: Activity?) {

                if (!openRadio) return

                //不需要的添加 window Activity
                if (activity is RadioActivity) return

                //检查是否已经存在 window ,如果存在则不添加 (针对某些生命周期或启动模式异常)
                activity?.let { mAttachActivity->
                    val mDecorView = mAttachActivity.window.decorView as FrameLayout
                    val mRootView = floatingWindows[mAttachActivity.hashCode()]?.mRootView
                    if (mRootView != null && mDecorView.indexOfChild( mRootView as View) != -1) {
                        return
                    }
                }

                activity?.let { attachActivity ->
                    val radioWindow : RadioFloatingWindow = RadioFloatingWindow::class.java.newInstance()
                    radioWindow.attach(Application,attachActivity)
                }
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }
        })
    }

    /**
     * 保存浮窗位置信息
     */
    fun saveWindowPosition(marginLeft : Int , marginTop : Int) {
        windowInfo.marginLeft = marginLeft
        windowInfo.marginTop = marginTop
    }

    /**
     * 保存浮窗Ui状态
     */
    fun saveWindowUiState(uiState: FloatingWindowInfo.UiState) {
        windowInfo.uiState = uiState
    }

    /**
     * 获取浮窗位置信息
     */
    fun obtainWindowMarginLeft() : Int {
        return windowInfo.marginLeft
    }

    fun obtainWindowMarginTop() : Int {
        return windowInfo.marginTop
    }

    /**
     * 获取浮窗Ui状态
     */
    fun obtainWindowUiState() : FloatingWindowInfo.UiState {
        return windowInfo.uiState
    }
}