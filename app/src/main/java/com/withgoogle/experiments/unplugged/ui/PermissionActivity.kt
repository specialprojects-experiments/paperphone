package com.withgoogle.experiments.unplugged.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView

class PermissionActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        val titleRes = intent.getIntExtra("title_res", 0)
        val rationaleRes = intent.getIntExtra("rationale_res", 0)
        val rationaleArea = getString(R.string.rationale_area, getString(rationaleRes))

        val title = getString(titleRes)

        with(findViewById<ModuleView>(R.id.module)) {
            setText(title[0].toString(), title)
            isChecked = true
        }

        findViewById<TextView>(R.id.rationale).text = rationaleArea

        findViewById<Button>(R.id.yes).setOnClickListener {
            startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            })
        }

        findViewById<Button>(R.id.no).setOnClickListener {
            finish()
        }
    }
}