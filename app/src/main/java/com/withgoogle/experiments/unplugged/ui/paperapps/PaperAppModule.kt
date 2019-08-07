package com.withgoogle.experiments.unplugged.ui.paperapps

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.annotation.DrawableRes
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.ui.pdf.MODULE_HEIGHT
import com.withgoogle.experiments.unplugged.ui.pdf.MODULE_WIDTH
import timber.log.Timber

class PaperAppModule(@DrawableRes val resId: Int): PdfModule {
    fun loadBitmap(resId: Int, resources: Resources): Bitmap {
        val image = BitmapFactory.decodeResource(resources, resId, BitmapFactory.Options().apply {
            inSampleSize = 1
        })
        Timber.d("Width: ${image.width}, Height: ${image.height}")

        val width = image.width
        val height = image.height
        val scaleWidth = MODULE_WIDTH / width

        val calculatedHeight = (height.toFloat() / width.toFloat()) * MODULE_HEIGHT

        val scaleHeight = calculatedHeight / height

        val matrix = Matrix()

        Timber.d("Width: ${image.width}, Calculated Height: $calculatedHeight")

        matrix.postScale(scaleWidth, scaleHeight)

        val resizedBitmap = Bitmap.createBitmap(
            image, 0, 0, width, height, matrix, true
        )
        image.recycle()
        return resizedBitmap
    }

    override fun draw(canvas: Canvas, resources: Resources) {
        loadBitmap(resId, resources).let {
            canvas.drawBitmap(it, 0F, 0F, Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
            })
        }
    }

    override val isRotated: Boolean
        get() = false
}