package com.pimuseum.demo.floatingwindow.core.info

import java.io.Serializable

open class FloatingWindowInfo : Serializable {

    /**
     * 浮窗坐标位置
     */
    var marginTop : Int = 0
    var marginLeft : Int = 0

    /**
     * 浮窗Ui 展开/折叠 状态
     */
    var uiState : UiState = UiState.Folded

    /**
     * 音频播放状态
     */
    enum class AudioState {
        Playing,Pause,Stop
    }

    /**
     * Ui展开折叠状态
     */
    enum class UiState{
        Expansion,Folded
    }

}