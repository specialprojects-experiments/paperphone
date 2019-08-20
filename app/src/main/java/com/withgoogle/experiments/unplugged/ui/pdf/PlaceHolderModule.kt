package com.withgoogle.experiments.unplugged.ui.pdf

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.withgoogle.experiments.unplugged.ui.PdfModule

class PlaceHolderModule(private val drawDots: Boolean = true): PdfModule {
    override suspend fun setupData() {

    }

    override fun draw(canvas: Canvas, resources: Resources) {
        val dotsPaint = Paint().apply {
            color = Color.BLACK
        }

        if (drawDots) {
            for (i in 0..14) {
                for (j in 0..9) {
                    canvas.drawCircle(j * 17F, i * 17F, 0.3F, dotsPaint)
                }
            }
        }
    }

    override val isRotated: Boolean
        get() = false
}