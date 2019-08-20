package com.withgoogle.experiments.unplugged.ui.print

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.AppState
import com.withgoogle.experiments.unplugged.ui.pdf.PdfGenerator
import com.withgoogle.experiments.unplugged.util.bindView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PdfActionSelectionActivity: AppCompatActivity() {
    private val savePdf by bindView<Button>(R.id.save_pdf)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_action)

        findViewById<Button>(R.id.print).setOnClickListener {
            startActivity(Intent(this, FoldingInfoActivity::class.java))
        }

        findViewById<Button>(R.id.save_pdf).setOnClickListener {
            it.isEnabled = false
            savePdf()
        }
    }

    private fun savePdf() {
        val generator = PdfGenerator(this)

        AppState.modules.value?.let { modules ->
            CoroutineScope(Dispatchers.Main).launch {
                val file = withContext(Dispatchers.IO) {
                    modules.first.forEach { it.setupData() }
                    modules.second?.setupData()

                    generator.generatePdf(modules.first, modules.second, modules.third)
                }
                openFileIntent(file)
                savePdf.isEnabled = true
            }
        }
    }

    private fun openFileIntent(file: File) {
        val fileUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.type = "application/pdf"
        intent.data = fileUri
        //intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        val chooser = Intent.createChooser(intent, "")
        startActivity(chooser)
    }

}