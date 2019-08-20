package com.withgoogle.experiments.unplugged.ui.calendar

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.model.Event
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.ui.pdf.MODULE_WIDTH
import com.withgoogle.experiments.unplugged.util.toTime
import java.time.Instant

class CalendarModule(private val events: List<Event>): PdfModule {
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

        canvas.drawText("CALENDAR", 0F, height, titleTextPaint)

        canvas.translate(0F, height + 20F)

        canvas.drawLine(0F, 0F, MODULE_WIDTH, 0F, Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 0.5F
        })

        val timeTextPaint = Paint().apply {
            textSize = 6.47F
            color = Color.BLACK
            letterSpacing = 0.12F
            typeface = resources.getFont(R.font.worksans_regular)
        }

        val timeMetrics = timeTextPaint.fontMetrics
        val timeHeight = timeMetrics.descent - timeMetrics.ascent

        val eventTextPaint = Paint().apply {
            textSize = 9F
            color = Color.BLACK
            typeface = resources.getFont(R.font.varela_regular)
        }

        val eventMetrics = timeTextPaint.fontMetrics
        val eventHeight = eventMetrics.descent - eventMetrics.ascent

        val maxEvents = if (events.size > 6) 6 else events.size

        for(i in 0..maxEvents) {
            val dateStart = Instant.ofEpochMilli(events[i].dateStart)
            val dateEnd = Instant.ofEpochMilli(events[i].dateEnd)

            val subString = eventTextPaint.breakText(events[i].title, 0, events[i].title.count(), true, MODULE_WIDTH, null)

            canvas.translate(0F, 24F)
            canvas.drawText(events[i].title.substring(0, subString), 0F, eventHeight, eventTextPaint)

            canvas.translate(0F, 8F)
            canvas.drawText("${dateStart.toTime()} - ${dateEnd.toTime()}", 0F, timeHeight, timeTextPaint)
        }
    }

    override val isRotated: Boolean
        get() = true
}