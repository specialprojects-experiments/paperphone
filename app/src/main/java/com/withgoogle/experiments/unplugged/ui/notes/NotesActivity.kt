package com.withgoogle.experiments.unplugged.ui.notes

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView

class NotesActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        with(findViewById<ModuleView>(R.id.module)) {
            setText("N", "Notes")
            isChecked = true
        }

        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }
    }
}