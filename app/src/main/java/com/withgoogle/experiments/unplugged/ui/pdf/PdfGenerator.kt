package com.withgoogle.experiments.unplugged.ui.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Environment
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import androidx.core.graphics.withSave
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.AppState
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.util.toExtent
import timber.log.Timber
import java.io.File
import java.time.Instant
import java.time.LocalDate

const val PAGE_MARGINS = 28F
const val PADDING_1 = 58F
const val PADDING_2 = 54F

const val MODULE_WIDTH = 154F
const val MODULE_HEIGHT = 242F

class PdfGenerator(private val context: Context, private val drawBorders: Boolean = false) {
    fun generatePdf(list: List<PdfModule>, mapModule: PdfModule? = null, contactlessModule: PdfModule? = null): File {
        val document = PrintedPdfDocument(context, PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape())
            .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
            .setResolution(PrintAttributes.Resolution("best","best", 300, 300))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build())

        val page = document.startPage(0)

        val canvas = page.canvas

        Timber.d( "Width: ${canvas.width}")
        Timber.d( "Height: ${canvas.height}")

        canvas.translate(PAGE_MARGINS, PAGE_MARGINS)

        list.forEachIndexed { index, pdfModule ->
            canvas.withSave {
                moveToNext(index)?.let {
                    Timber.d("Moving to: (${it.x}, ${it.y})")
                    translate(it.x, it.y)
                }

                if (drawBorders) {
                    drawBorder(canvas)
                }

                if (pdfModule.isRotated) {
                    translate(MODULE_WIDTH, MODULE_HEIGHT)
                    rotate(-180F)
                }

                pdfModule.draw(canvas, context.resources)
            }
        }

        contactlessModule?.draw(canvas, context.resources)

        drawFoldingHints(canvas)

        // finish the page
        document.finishPage(page)

        mapModule?.let {
            val mapPage = document.startPage(1)

            mapPage.canvas.translate(PAGE_MARGINS, PAGE_MARGINS)

            it.draw(mapPage.canvas, context.resources)

            document.finishPage(mapPage)
        }

        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        // Clear old files
        val children = filesDir?.list()
        children?.let {
            for (fileName in it) {
                File(filesDir, fileName).delete()
            }
        }

        val filename = context.getString(R.string.filename, AppState.firstName.value, LocalDate.now().toExtent())

        val fileDestination = File(filesDir, "$filename.pdf")

        document.writeTo(fileDestination.outputStream())
        document.close()

        return fileDestination
    }

    private fun drawFoldingHints(canvas: Canvas) {
        val hintsPaint = Paint().apply {
            color = Color.BLACK
        }

        with(canvas) {
            translate(601F, 0F)
            drawRect(0F, 0F, 5F, 5F, hintsPaint)

            translate(0F, 537F)
            drawRect(0F, 0F, 5F, 5F, hintsPaint)
        }
    }

    private fun drawBorder(canvas: Canvas) {
        canvas.drawRect(0F, 0F, MODULE_WIDTH, MODULE_HEIGHT, Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.5F
        })
    }

    private fun moveToNext(index: Int): PointF? {
        val topHeight = 0F
        val lowHeight = MODULE_HEIGHT + PADDING_1

        return when(index) {
            1 -> {
                return PointF(MODULE_WIDTH + PADDING_1, topHeight)
            }
            2 -> {
                return PointF(MODULE_WIDTH * 2 + (PADDING_1 + PADDING_2), topHeight)
            }
            3 -> {
                return PointF(MODULE_WIDTH * index + PADDING_1 * 2 + PADDING_2, topHeight)
            }
            4 -> {
                return PointF(0F, lowHeight)
            }
            5 -> {
                return PointF(MODULE_WIDTH + PADDING_1, lowHeight)
            }
            6 -> {
                return PointF(MODULE_WIDTH * 2 + (PADDING_1 + PADDING_2), lowHeight)
            }
            7 -> {
                return PointF(MODULE_WIDTH * (index - 4) + PADDING_1 * 2 + PADDING_2, lowHeight)
            }
            else -> PointF(0F, 0F)
        }
    }
}