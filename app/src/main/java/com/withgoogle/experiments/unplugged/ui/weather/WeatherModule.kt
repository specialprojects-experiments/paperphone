package com.withgoogle.experiments.unplugged.ui.weather

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.content.res.ResourcesCompat
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.weather.WeatherDataSource
import com.withgoogle.experiments.unplugged.model.ThreeHourForecast
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.ui.pdf.MODULE_WIDTH
import com.withgoogle.experiments.unplugged.util.toTime
import com.withgoogle.experiments.unplugged.util.weatherFormat
import java.time.LocalDate
import android.graphics.drawable.VectorDrawable
import androidx.core.graphics.withSave

private const val ICON_SIZE = 19F

class WeatherModule(
    val forecasts: List<ThreeHourForecast>,
    val date: LocalDate,
    val location: String = WeatherDataSource.location
): PdfModule {
    override suspend fun setupData() {

    }

    override fun draw(canvas: Canvas, resources: Resources) {
        drawTitle(canvas, resources)

        drawLocationDate(canvas, resources)

        drawForecasts(canvas, resources)
    }

    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    private fun drawForecasts(canvas: Canvas, resources: Resources) {
        canvas.translate(0F, 32F)

        val timeOfDayPaint = Paint().apply {
            textSize = 8.5F
            color = Color.BLACK
            letterSpacing = 0.12F
            typeface = resources.getFont(R.font.varela_regular)
        }

        val fontMetrics = timeOfDayPaint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent

        val linePaint = Paint().apply {
            strokeWidth = 0.5F
            color = Color.BLACK
        }

        val temperaturePaint = Paint().apply {
            textSize = 8.5F
            color = Color.BLACK
            letterSpacing = 0.05F
            typeface = resources.getFont(R.font.varela_regular)
        }

        val tempMetrics = temperaturePaint.fontMetrics
        val tempHeight = tempMetrics.descent - tempMetrics.ascent

        val descriptionPaint = Paint().apply {
            textSize = 6F
            color = Color.BLACK
            typeface = resources.getFont(R.font.worksans_regular)
        }

        val descMetrics = descriptionPaint.fontMetrics
        val descHeight = descMetrics.descent - descMetrics.ascent

        forecasts.forEach { threeHourForecast ->
            val time = threeHourForecast.timestamp.toTime()

            with(canvas) {
                drawText(getTimeOfDay(time), 0F, fontHeight, timeOfDayPaint)
                translate(0F, fontHeight + 4F)

                drawLine(0F, 0F, MODULE_WIDTH, 0F, linePaint)

                withSave {
                    val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_cloudy, null) as VectorDrawable

                    drawable.let {
                        val bitmap = getBitmap(drawable)

                        val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
                        val destRect = RectF(0F, 6F, ICON_SIZE, 6F + ICON_SIZE)

                        canvas.drawBitmap(bitmap, srcRect, destRect, Paint())
                    }

                    translate(26F, 3F)

                    // Draw temperature
                    drawText("${threeHourForecast.temperature}º", 0F, tempHeight, temperaturePaint)

                    translate(0F, 2F + tempHeight)
                    // Draw description
                    drawText(threeHourForecast.weather, 0F, descHeight, descriptionPaint)
                }

                translate(0F, 35F)
            }

        }
    }

    fun getTimeOfDay(input: String): String {
        return when(input) {
            "09:00" -> "MORNING"
            "12:00" -> "AFTERNOON"
            "18:00" -> "EVENING"
            else -> ""
        }
    }

    fun drawLocationDate(canvas: Canvas, resources: Resources) {
        val paint = Paint().apply {
            textSize = 7.31F
            color = Color.BLACK
            typeface = resources.getFont(R.font.worksans_regular)
        }

        val fontMetrics = paint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent

        with(canvas) {
            drawText(location, 0F, fontHeight, paint)

            translate(0F, fontHeight + 2F)

            drawText(date.weatherFormat(), 0F, fontHeight, paint)

            translate(0F, fontHeight)
        }

    }

    fun drawTitle(canvas: Canvas, resources: Resources) {
        val paint = Paint().apply {
            textSize = 11.33F
            color = Color.BLACK
            letterSpacing = 0.1F
            typeface = resources.getFont(R.font.varela_regular)
        }

        val fm = paint.fontMetrics
        val height = fm.descent - fm.ascent

        canvas.drawText("WEATHER", 0F, height, paint)
        canvas.translate(0F, height + 10F)
    }

    override val isRotated: Boolean
        get() = false
}