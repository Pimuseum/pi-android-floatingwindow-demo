package com.pimuseum.demo.floatingwindow.core.windows

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.airbnb.lottie.LottieAnimationView
import com.pimuseum.demo.floatingwindow.core.FloatingWindowKit
import com.pimuseum.demo.floatingwindow.core.inter.AbstractFloatingWindow
import com.pimuseum.demo.floatingwindow.core.info.FloatingWindowInfo
import com.pimuseum.demo.floatingwindow.core.view.FloatingWindowLayout
import com.pimuseum.demo.floatingwindow.R
import com.pimuseum.demo.floatingwindow.activity.RadioActivity
import com.pimuseum.demo.floatingwindow.core.helper.ScreenUtils
import com.pimuseum.demo.floatingwindow.core.helper.gone
import com.pimuseum.demo.floatingwindow.core.helper.visible

class RadioFloatingWindow : AbstractFloatingWindow() {

    private var expandWidth = 0
    private var foldedWidth = 0
    private var controllerBtnWidth = 0

    private var audioState: FloatingWindowInfo.AudioState = FloatingWindowInfo.AudioState.Playing
    private var uiState: FloatingWindowInfo.UiState = FloatingWindowInfo.UiState.Folded

    private var ivDiskImage: ImageView? = null
    private var diskAnimView: LottieAnimationView? = null
    private var rlDisk: RelativeLayout? = null
    private var ivPlayController: ImageView? = null
    private var ivClose: ImageView? = null

    override fun onCreateWindow(context: Context, rootView: FrameLayout): View {
        val view = LayoutInflater.from(context).inflate(R.layout.view_radio_floating_window, rootView, false)
        expandWidth = ScreenUtils.dp2px(context, 120F)
        foldedWidth = ScreenUtils.dp2px(context, 43F)
        windowHeight = ScreenUtils.dp2px(context, 43F)
        windowMargin = ScreenUtils.dp2px(context, 5F)
        controllerBtnWidth = ScreenUtils.dp2px(context, 35F)
        return view
    }

    override fun onWindowCreated(rootView: FrameLayout) {

        ivDiskImage = rootView.findViewById(R.id.ivDiskImage)
        diskAnimView = rootView.findViewById(R.id.diskAnimView)

        rlDisk = rootView.findViewById(R.id.rlDisk)
        ivPlayController = rootView.findViewById(R.id.ivPlayController)
        ivClose = rootView.findViewById(R.id.ivClose)

        //根据当前内存保存的Ui状态 设置 windowWidth
        uiState = FloatingWindowKit.obtainWindowUiState()
        windowWidth = if (uiState == FloatingWindowInfo.UiState.Expansion) {
            ivPlayController?.visible()
            ivClose?.visible()
            expandWidth
        } else {
            ivPlayController?.gone()
            ivClose?.gone()
            foldedWidth
        }
    }

    override fun onAreaClick(xInParent: Int, yInParent: Int) {
        if (xInParent in 1..foldedWidth) {
            switchUi()
        } else if (xInParent > foldedWidth && xInParent <= (foldedWidth + controllerBtnWidth)) {
            audioControl()
        } else if (xInParent > (foldedWidth + controllerBtnWidth) && xInParent <= (foldedWidth + 2 * controllerBtnWidth)) {
            FloatingWindowKit.openRadio = false
            detach()
        }
    }

    override fun attach(context: Context, activity: Activity) {
        super.attach(context, activity)
    }

    override fun detach() {
        super.detach()
    }

    override fun onResume(mDecorView: FrameLayout?) {
        super.onResume(mDecorView)
        //设置监听点击浮窗以外的区域
        if (mRootView !is FloatingWindowLayout) return
        (mRootView as FloatingWindowLayout).setOnTouchInDecorViewListener(object : FloatingWindowLayout.OnTouchInDecorView{
            override fun onTouch(rawX: Int, rawY: Int) {
                if (uiState == FloatingWindowInfo.UiState.Expansion) {
                    floatingWindowParams?.let { param->
                        if (rawX < param.leftMargin || rawX > param.leftMargin + expandWidth ||
                                rawY < param.topMargin || rawY > param.topMargin + windowHeight) {

                            //折叠悬浮窗
                            windowWidth = foldedWidth
                            uiState = FloatingWindowInfo.UiState.Folded
                            adjustWindowPosition()
                            ivPlayController?.gone()
                            ivClose?.gone()
                        }
                    }
                }
            }
        })
    }

    override fun onDestroy(mDecorView: FrameLayout?) {
        super.onDestroy(mDecorView)
        if (mRootView !is FloatingWindowLayout) return
        (mRootView as FloatingWindowLayout).removeOnTouchInDecorViewListener()
    }

    override fun adjustWindowPosition() {
        super.adjustWindowPosition()
        FloatingWindowKit.saveWindowUiState(uiState)
    }

    /**********************************************       Logic            **************************************************/

    private fun switchUi() {
        if (this.uiState == FloatingWindowInfo.UiState.Expansion) {
            mAttachActivity?.get()?.let {
                //折叠悬浮窗
                windowWidth = foldedWidth
                uiState = FloatingWindowInfo.UiState.Folded
                adjustWindowPosition()
                ivPlayController?.gone()
                ivClose?.gone()
                //跳转到电台页
                mAttachActivity?.get()?.let{
                    it.startActivity(Intent(it,RadioActivity::class.java))
                }
            }
        } else {
            windowWidth = expandWidth
            this.uiState = FloatingWindowInfo.UiState.Expansion
            adjustWindowPosition()
            ivPlayController?.visible()
            ivClose?.visible()
        }
    }

    private fun audioControl() {
        if (audioState == FloatingWindowInfo.AudioState.Playing) {
            audioState = FloatingWindowInfo.AudioState.Pause
            diskAnimView?.pauseAnimation()
            ivPlayController?.setImageResource(R.mipmap.icon_controller_play)
        } else {
            audioState = FloatingWindowInfo.AudioState.Playing
            diskAnimView?.resumeAnimation()
            ivPlayController?.setImageResource(R.mipmap.icon_controller_pause)
        }
    }

}