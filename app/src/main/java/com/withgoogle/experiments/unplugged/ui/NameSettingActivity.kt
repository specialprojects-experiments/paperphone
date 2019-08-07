package com.withgoogle.experiments.unplugged.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.withgoogle.experiments.unplugged.PaperPhoneApp
import com.withgoogle.experiments.unplugged.R

class NameSettingActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_setting)

        val input = findViewById<EditText>(R.id.input)
        input.focusSearch(View.FOCUS_DOWN)

        AppState.firstName.observe(this, Observer {
            (input as TextView).text = it
        })

        findViewById<Button>(R.id.ok).setOnClickListener {
            val name = input.editableText.toString()

            if (name.isNotEmpty()) {
                AppState.firstName.postValue(name)
                PaperPhoneApp.obtain(this).namePreference.set(name)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}