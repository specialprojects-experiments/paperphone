package com.withgoogle.experiments.unplugged.ui.contactless

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView

class ContactlessActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactless)

        with(findViewById<ModuleView>(R.id.module)) {
            setText("C", "Contactless")
            isChecked = true
        }

        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }
    }
}