package com.pimuseum.demo.floatingwindow.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pimuseum.demo.floatingwindow.R
import com.pimuseum.demo.floatingwindow.core.FloatingWindowKit
import com.pimuseum.demo.floatingwindow.core.helper.noFastClick
import kotlinx.android.synthetic.main.activity_radio.*

class RadioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio)

        FloatingWindowKit.openRadio = true

        tvGoBack.setOnClickListener {
            it.noFastClick()
            finish()
        }
    }
}
