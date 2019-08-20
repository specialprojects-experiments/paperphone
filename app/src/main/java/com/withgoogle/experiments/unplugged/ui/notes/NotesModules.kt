package com.withgoogle.experiments.unplugged.ui.notes

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withSave
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.PdfModule

class NotesModules: PdfModule {
    override suspend fun setupData() {

    }

    override fun draw(canvas: Canvas, resources: Resources) {
        val titleTextPaint = Paint().apply {
            textSize = 11.33F
            color = Color.BLACK
            letterSpacing = 0.1F
            typeface = resources.getFont(R.font.varela_regular)
        }

        val fm = titleTextPaint.fontMetrics
        val height = fm.descent - fm.ascent

        val dotsPaint = Paint().apply {
            color = Color.BLACK
        }


        with(canvas) {
            drawText(resources.getString(R.string.notes).toUpperCase(), 0F, height, titleTextPaint)

            translate(0F, 38F)
            withSave {
                for (i in 0..12) {
                    for (j in 0..9) {
                        canvas.drawCircle(j * 17F, i * 17F, 0.3F, dotsPaint)
                    }
                }
            }
        }
    }

    override val isRotated: Boolean
        get() = false
}