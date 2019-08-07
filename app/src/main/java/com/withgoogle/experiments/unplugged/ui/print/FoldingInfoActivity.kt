package com.withgoogle.experiments.unplugged.ui.print

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.withgoogle.experiments.unplugged.R

class FoldingInfoActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folding)

        findViewById<Button>(R.id.done).setOnClickListener {
            finishAffinity()
        }

        with(findViewById<ImageView>(R.id.animation)) {
            val frameAnimation = drawable as AnimationDrawable

            frameAnimation.start()
        }
    }
}