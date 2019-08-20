package com.withgoogle.experiments.unplugged.ui.tasks

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withSave
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.model.TaskItem
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.ui.pdf.MODULE_WIDTH
import com.withgoogle.experiments.unplugged.util.isToday

private const val TASKS_MARGIN = 16F

class TasksModule(val tasks: List<TaskItem>): PdfModule {
    private var data: List<TaskItem>? = null

    override suspend fun setupData() {
        data = tasks
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

        val circlePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.5F
        }

        canvas.drawText("TASKS", 0F, height, titleTextPaint)

        canvas.withSave {
            canvas.translate(0F, height + 13F)

            val timeTextPaint = Paint().apply {
                textSize = 6.47F
                color = Color.BLACK
                typeface = resources.getFont(R.font.worksans_regular)
            }

            val timeMetrics = timeTextPaint.fontMetrics
            val timeHeight = timeMetrics.descent - timeMetrics.ascent

            val descriptionTextPaint = Paint().apply {
                textSize = 9F
                color = Color.BLACK
                typeface = resources.getFont(R.font.varela_regular)
            }

            val descriptionMetrics = descriptionTextPaint.fontMetrics
            val descriptionHeight = descriptionMetrics.descent - descriptionMetrics.ascent

            data?.forEach {
                drawCircle(2.9F, 8.5F, 2.9F, circlePaint)

                val subString = descriptionTextPaint.breakText(it.title, 0, it.title.count(), true, MODULE_WIDTH - TASKS_MARGIN, null)

                drawText(it.title.substring(0, subString), TASKS_MARGIN, descriptionHeight, descriptionTextPaint)

                it.dueDate?.let { dueDate ->
                    val timeRelative = if (dueDate.isToday()) {
                        "Today"
                    } else {
                        "Tomorrow"
                    }
                    translate(0F, descriptionHeight + 2F)
                    drawText(timeRelative, TASKS_MARGIN, timeHeight, timeTextPaint)
                }

                translate(0F, 14F)
            }
        }

        drawPlaceholder(canvas, circlePaint)
    }

    private fun drawPlaceholder(
        canvas: Canvas,
        circlePaint: Paint
    ) {
        with(canvas) {
            translate(0F, 162F)

            for (i in 0..2) {
                drawCircle(2.9F, 8.5F, 2.9F, circlePaint)

                translate(0F, 14F)
            }
        }
    }

    override val isRotated: Boolean
        get() = false
}