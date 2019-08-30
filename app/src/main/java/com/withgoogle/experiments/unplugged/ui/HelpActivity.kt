package com.withgoogle.experiments.unplugged.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.withgoogle.experiments.unplugged.R

class HelpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        findViewById<Button>(R.id.ok).setOnClickListener {
            finish()
        }
    }
}