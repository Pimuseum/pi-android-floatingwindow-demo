package com.pimuseum.demo.floatingwindow.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pimuseum.demo.floatingwindow.R
import com.pimuseum.demo.floatingwindow.core.helper.noFastClick
import kotlinx.android.synthetic.main.activity_anyway.tvGoRadio

class AnywayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anyway)
        tvGoRadio.setOnClickListener {
            it.noFastClick()
            startActivity(Intent(this,RadioActivity::class.java))
        }
    }
}
