package com.withgoogle.experiments.unplugged.ui.photos

import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import com.withgoogle.experiments.unplugged.ui.PdfModule
import timber.log.Timber

private const val WIDTH_RESIZE = 117.5F

class PhotoModule(private val uri: Uri): PdfModule {
    private var photoBitmap: Bitmap? = null

    override suspend fun setupData() {
        val image = BitmapFactory.decodeFile(uri.toString(), BitmapFactory.Options().apply {
            inSampleSize = 1
        })
        Timber.d("Width: ${image.width}, Height: ${image.height}")

        photoBitmap = image
    }

    override fun draw(canvas: Canvas, resources: Resources) {
        photoBitmap?.let {
            val ma = ColorMatrix().apply {
                setSaturation(0f)
            }

            val width = it.width
            val height = it.height

            val calculatedHeight = (height.toFloat() / width.toFloat()) * WIDTH_RESIZE

            val srcRect = Rect(0, 0, width, height)
            val destRect = RectF(0F, 0F, WIDTH_RESIZE, calculatedHeight)

            canvas.drawBitmap(it, srcRect, destRect, Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
                colorFilter = ColorMatrixColorFilter(ma)
            })
        }
    }

    override val isRotated: Boolean
        get() = false
}