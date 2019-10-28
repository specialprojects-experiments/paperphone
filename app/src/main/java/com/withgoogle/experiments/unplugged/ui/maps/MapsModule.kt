package com.withgoogle.experiments.unplugged.ui.maps

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.core.graphics.withSave
import androidx.core.graphics.withTranslation
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.maps.GoogleDirections
import com.withgoogle.experiments.unplugged.data.integrations.maps.GoogleHttpClient
import com.withgoogle.experiments.unplugged.model.Location
import com.withgoogle.experiments.unplugged.ui.PdfModule
import okhttp3.Request
import timber.log.Timber

//private const val MAP_SIZE = 493F
private const val DIRECTION_INFO_WIDTH = 256F
private const val DIRECTION_INFO_HEIGHT = 493F

private const val CANVAS_PIVOT_X = 787F
private const val CANVAS_PIVOT_Y = 541F

class MapsModule(val context: Context, val origin: Location, val destination: Location): PdfModule {
    private var mapBitmap: Bitmap? = null

    override suspend fun setupData() {
        val mapUrl = GoogleDirections().directionEncodedPath(origin, destination)

        mapUrl?.let { mapBitmap = loadMapAsBitmap(it) }
    }

    override fun draw(canvas: Canvas, resources: Resources) {
        canvas.translate(CANVAS_PIVOT_X, CANVAS_PIVOT_Y)
        canvas.rotate(-180F)

        drawFoldHints(canvas)

        drawTitle(canvas, resources)

        drawMap(canvas)

        drawDirectionsInfo(canvas, resources)

        drawDots(canvas)
    }

    private fun loadMapAsBitmap(url: String): Bitmap? {
        val request = Request.Builder()
            .url(url)
            .build()

        val response = GoogleHttpClient.okHttpClient.newCall(request).execute()

        return if (response.isSuccessful) {
            BitmapFactory.decodeStream(response.body()?.byteStream())
        } else {
            null
        }
    }

    private fun drawMap(canvas: Canvas) {
        canvas.withTranslation(294F, 28F) {
            mapBitmap?.let {
                Timber.d("Drawing bitmap with size: ${it.width},${it.height}")
                val ma = ColorMatrix().apply {
                    setSaturation(0f)
                }

                drawBitmap(it, 0F, 0F, Paint().apply {
                    isAntiAlias = true
                    colorFilter = ColorMatrixColorFilter(ma)
                })
            }
        }
    }

    private fun drawDirectionsInfo(canvas: Canvas, resources: Resources) {
        canvas.withTranslation(0F, 28F) {
            val originAddress = origin.address
            val destAddress = destination.address

            drawCircles(canvas, resources)

            // Draw origin text
            withTranslation(24F, 0F) {
                drawDirectionAndAddress(canvas,"LEAVING FROM", originAddress, resources)

                translate(0F, 37F)
                drawDirectionAndAddress(canvas,"GOING TO", destAddress, resources)
            }
        }
    }

    private fun drawDots(canvas: Canvas) {
        val dotsPaint = Paint().apply {
            color = Color.BLACK
        }

        with(canvas) {
            withTranslation(0F, 130F) {
                for (i in 0..23) {
                    for (j in 0..16) {
                        drawCircle(j * 17F, i * 17F, 0.3F, dotsPaint)
                    }
                }
            }
        }
    }

    private fun drawCircles(canvas: Canvas, resources: Resources) {
        canvas.withSave {
            val hintsPaint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.FILL_AND_STROKE
            }

            val dotsPaint = Paint().apply {
                color = Color.BLACK
            }

            translate(0F, 3F)
            drawCircle(7F, 7F, 7F,  hintsPaint)

            translate(7F, 20F)
            for (it in 0..4) {
                drawCircle(0F, 0F, 0.3F,  dotsPaint)

                translate(0F, 5F)
            }
            translate(-7F, 0F)

            drawCircle(7F, 7F, 7F,  hintsPaint)
        }

        canvas.withSave {
            val letterTextPaint = Paint().apply {
                textSize = 8.36F
                color = Color.WHITE
                typeface = resources.getFont(R.font.varela_regular)
            }

            val fm = letterTextPaint.fontMetrics
            val height = fm.descent - fm.ascent

            translate(0F, 2F)
            drawText("A", 4F, height, letterTextPaint)
            translate(0F, 45F)
            drawText("B", 4F, height, letterTextPaint)
        }
    }

    private fun drawDirectionAndAddress(canvas: Canvas, label: String, address: String, resources: Resources) {
        val directionTextPaint = Paint().apply {
            textSize = 6F
            color = Color.BLACK
            letterSpacing = 0.12F
            typeface = resources.getFont(R.font.varela_regular)
        }

        val placeTextPaint = Paint().apply {
            textSize = 9F
            color = Color.BLACK
            typeface = resources.getFont(R.font.worksans_regular)
        }

        val dirFM = directionTextPaint.fontMetrics
        val dirHeight = dirFM.descent - dirFM.ascent

        with(canvas) {
            drawText(label, 0F, dirHeight, directionTextPaint)

            translate(0F, 8F)

            val placeFM = placeTextPaint.fontMetrics
            val placeheight = placeFM.descent - placeFM.ascent

            val addressSplitted = address.split(",")
            drawText(addressSplitted[0], 0F, placeheight, placeTextPaint)

            if (addressSplitted.size > 2) {
                drawText("${addressSplitted[1].trim()}, ${addressSplitted[2]}", 0F, placeheight + 10F, placeTextPaint)
            }
        }
    }

    private fun drawTitle(canvas: Canvas, resources: Resources) {
        val titleTextPaint = Paint().apply {
            textSize = 11.33F
            color = Color.BLACK
            letterSpacing = 0.1F
            typeface = resources.getFont(R.font.varela_regular)
        }

        val fm = titleTextPaint.fontMetrics
        val height = fm.descent - fm.ascent

        canvas.drawText("MAPS", 0F, height, titleTextPaint)
    }

    private fun drawFoldHints(canvas: Canvas) {
        canvas.withTranslation(0F, 535F) {
            val hintsPaint = Paint().apply {
                color = Color.BLACK
            }

            drawCircle(3F, 3F, 3F,  hintsPaint)

            translate(782F, 0F)
            drawCircle(0F, 0F, 3F, hintsPaint)
        }
    }

    override val isRotated: Boolean
        get() = false
}