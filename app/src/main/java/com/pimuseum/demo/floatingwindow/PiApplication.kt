package com.pimuseum.demo.floatingwindow

import android.app.Application
import com.pimuseum.demo.floatingwindow.core.FloatingWindowKit

class PiApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FloatingWindowKit.install(this)
    }
}