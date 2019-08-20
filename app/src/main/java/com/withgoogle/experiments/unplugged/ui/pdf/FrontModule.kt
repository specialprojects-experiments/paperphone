package com.withgoogle.experiments.unplugged.ui.pdf

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withSave
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.util.toExtent
import java.time.LocalDate

class FrontModule(private val firstName: String): PdfModule {
    override suspend fun setupData() {

    }

    override fun draw(canvas: Canvas, resources: Resources) {
        val titleTextPaint = Paint().apply {
            textSize = 17.78F
            color = Color.BLACK
            letterSpacing = 0.1F
            typeface = resources.getFont(R.font.varela_regular)
        }

        val fm = titleTextPaint.fontMetrics
        val height = fm.descent - fm.ascent

        val dateTextPaint = Paint().apply {
            textSize = 9F
            color = Color.BLACK
            typeface = resources.getFont(R.font.varela_regular)
        }

        with(canvas) {
            drawText("${firstName.toUpperCase()}'S", 0F, height, titleTextPaint)

            withSave {
                translate(0F, height)

                drawText("PHONE", 0F, height, titleTextPaint)

            }

            translate(0F, MODULE_HEIGHT)
            drawText(LocalDate.now().toExtent(), 0F, 0F, dateTextPaint)
        }
    }

    override val isRotated: Boolean
        get() = false
}