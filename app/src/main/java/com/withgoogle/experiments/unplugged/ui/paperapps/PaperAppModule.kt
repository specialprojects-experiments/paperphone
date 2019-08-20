package com.withgoogle.experiments.unplugged.ui.paperapps

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.DrawableRes
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.ui.pdf.MODULE_HEIGHT
import com.withgoogle.experiments.unplugged.ui.pdf.MODULE_WIDTH

class PaperAppModule(@DrawableRes val resId: Int): PdfModule {
    override suspend fun setupData() {

    }

    override fun draw(canvas: Canvas, resources: Resources) {
        val image = BitmapFactory.decodeResource(resources, resId, BitmapFactory.Options().apply {
            inSampleSize = 1
        })

        with(image) {
            val srcRect = Rect(0, 0, width, height)
            val destRect = RectF(0F, 0F, MODULE_WIDTH, MODULE_HEIGHT)
            canvas.drawBitmap(this, srcRect, destRect, Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
            })
        }
    }

    override val isRotated: Boolean
        get() = false
}