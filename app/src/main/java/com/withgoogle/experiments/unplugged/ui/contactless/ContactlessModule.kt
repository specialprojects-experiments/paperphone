package com.withgoogle.experiments.unplugged.ui.contactless

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import androidx.core.graphics.withTranslation
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.ui.pdf.PAGE_MARGINS

class ContactlessModule: PdfModule {
    override suspend fun setupData() {

    }

    override fun draw(canvas: Canvas, resources: Resources) {
        val linePaint =  Paint().apply {
            color = resources.getColor(R.color.black, null)
            strokeWidth = 0.5F
            pathEffect = DashPathEffect( floatArrayOf(3F, 2.6F), 0F)
        }

        canvas.withTranslation(-PAGE_MARGINS, -PAGE_MARGINS) {
            drawLine(650F, 358F, 688F, 320F, linePaint)
            drawLine(785F, 320F, 823F, 358F, linePaint)
            drawLine(650F, 541F, 688F, 580F, linePaint)
            drawLine(785F, 580F, 823F, 541F, linePaint)
        }
    }

    override val isRotated: Boolean
        get() = false
}