package com.withgoogle.experiments.unplugged.ui.pdf

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import com.withgoogle.experiments.unplugged.ui.PdfModule
import timber.log.Timber
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

private const val WIDTH_RESIZE = 117.5F

class PhotoModule(private val context: Context, private val uri: Uri): PdfModule {
    fun loadBitmap(): Bitmap? {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")

        parcelFileDescriptor?.use {
            val image = BitmapFactory.decodeFileDescriptor(it.fileDescriptor, null, BitmapFactory.Options().apply {
                inSampleSize = 4
            })
            Timber.d("Width: ${image.width}, Height: ${image.height}")

            val width = image.width
            val height = image.height
            val scaleWidth = WIDTH_RESIZE / width

            val calculatedHeight = (height.toFloat() / width.toFloat()) * WIDTH_RESIZE

            val scaleHeight = calculatedHeight / height

            val matrix = Matrix()

            matrix.postScale(scaleWidth, scaleHeight)

            val resizedBitmap = Bitmap.createBitmap(
                image, 0, 0, width, height, matrix, true
            )
            image.recycle()
            return resizedBitmap
        }

        return null
    }

    override fun draw(canvas: Canvas, resources: Resources) {
        loadBitmap()?.let {
            val ma = ColorMatrix().apply {
                setSaturation(0f)
            }

            canvas.drawBitmap(it, 0F, 0F, Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
                colorFilter = ColorMatrixColorFilter(ma)
            })
        }
    }

    override val isRotated: Boolean
        get() = false
}