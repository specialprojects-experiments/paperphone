package com.withgoogle.experiments.unplugged.ui.photos

import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation
import com.withgoogle.experiments.unplugged.ui.PdfModule
import timber.log.Timber

private const val WIDTH_RESIZE = 117.5F

class PhotoModule(private val uri: Uri): PdfModule {
    private var photoBitmap: Bitmap? = null

    override suspend fun setupData() {
        val image = BitmapFactory.decodeFile(uri.toString(), BitmapFactory.Options().apply {
            inSampleSize = 2
        })
        Timber.d("Width: ${image.width}, Height: ${image.height}")

        photoBitmap = image
    }

    override fun draw(canvas: Canvas, resources: Resources) {
        photoBitmap?.let {
            val bitmapPaint = Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
                colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                    setSaturation(0f)
                })
            }

            val isLandscape = it.width > it.height

            val width = it.width
            val height = it.height

            val srcRect = Rect(0, 0, width, height)
            val destRect = calculateDestinationBoundary(width, height, isLandscape)

            with(canvas) {
                if (isLandscape) {
                    withTranslation(0F, destRect.width()) {
                        withRotation(-90F, 0F, 0F) {
                            drawBitmap(it, srcRect, destRect, bitmapPaint)
                        }
                    }
                } else {
                    drawBitmap(it, srcRect, destRect, bitmapPaint)
                }
            }
        }
    }

    private fun calculateDestinationBoundary(width: Int, height: Int, isLandscape: Boolean): RectF {
        val calculated = if (!isLandscape) {
            (height.toFloat() / width.toFloat()) * WIDTH_RESIZE
        } else {
            (width.toFloat() / height.toFloat()) * WIDTH_RESIZE
        }

        return  if(!isLandscape) {
            RectF(0F, 0F, WIDTH_RESIZE, calculated)
        } else {
            RectF(0F, 0F, calculated, WIDTH_RESIZE)
        }
    }

    override val isRotated: Boolean
        get() = false
}