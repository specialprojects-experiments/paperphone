package com.withgoogle.experiments.unplugged.ui.print

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import androidx.core.graphics.withSave
import com.withgoogle.experiments.unplugged.ui.PdfModule
import com.withgoogle.experiments.unplugged.ui.pdf.*
import timber.log.Timber
import java.io.FileOutputStream
import java.io.IOException

class PaperPhoneDocumentAdapter(val context: Context,
                                val list: List<PdfModule>, val mapModule: PdfModule? = null,
                                val contactlessModule: PdfModule? = null): PrintDocumentAdapter() {
    var pages: Int = 0
    var pdfDocument: PrintedPdfDocument? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        pdfDocument = PrintedPdfDocument(context, newAttributes)

        // Respond to cancellation request
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        // Compute the expected number of printed pages
        val pages = if (mapModule != null) 2 else 1

        this.pages = pages

        if (pages > 0) {
            // Return print information to print framework
            PrintDocumentInfo.Builder("print_output.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages)
                .build()
                .also { info ->
                    // Content layout reflow is complete
                    callback.onLayoutFinished(info, true)
                }
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed.")
        }
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        // Iterate over each page of the document,
        // check if it's in the output range.
        for (i in 0 until this.pages) {
            // Check to see if this page is in the output range.
            pdfDocument?.startPage(i).also { page ->

                // check for cancellation
                if (cancellationSignal?.isCanceled == true) {
                    callback.onWriteCancelled()
                    pdfDocument?.close()
                    pdfDocument = null
                    return
                }

                // Draw page content for printing
                page?.let { drawPage(it) }

                // Rendering is complete, so page can be finalized.
                pdfDocument?.finishPage(page)
            }
        }

        // Write PDF document to file
        try {
            pdfDocument?.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: IOException) {
            callback.onWriteFailed(e.toString())
            return
        } finally {
            pdfDocument?.close()
            pdfDocument = null
        }

        // Signal the print framework the document is complete
        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }

    private fun drawPage(page: PdfDocument.Page) {
        if (page.info.pageNumber == 0) {
            val canvas = page.canvas

            canvas.translate(
                PAGE_MARGINS,
                PAGE_MARGINS
            )

            list.forEachIndexed { index, pdfModule ->
                canvas.withSave {
                    moveToNext(index)?.let {
                        Timber.d("Moving to: (${it.x}, ${it.y})")
                        translate(it.x, it.y)
                    }

                    if (pdfModule.isRotated) {
                        translate(
                            MODULE_WIDTH,
                            MODULE_HEIGHT
                        )
                        rotate(-180F)
                    }

                    pdfModule.draw(canvas, context.resources)
                }
            }

            contactlessModule?.draw(canvas, context.resources)

            drawFoldingHints(canvas)
        } else {
            page.canvas.translate(
                PAGE_MARGINS,
                PAGE_MARGINS
            )

            mapModule?.draw(page.canvas, context.resources)
        }
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

    private fun moveToNext(index: Int): PointF? {
        val topHeight = 0F
        val lowHeight = MODULE_HEIGHT + PADDING_1

        return when (index) {
            1 -> {
                return PointF(
                    MODULE_WIDTH + PADDING_1,
                    topHeight
                )
            }
            2 -> {
                return PointF(
                    MODULE_WIDTH * 2 + (PADDING_1 + PADDING_2),
                    topHeight
                )
            }
            3 -> {
                return PointF(
                    MODULE_WIDTH * index + PADDING_1 * 2 + PADDING_2,
                    topHeight
                )
            }
            4 -> {
                return PointF(0F, lowHeight)
            }
            5 -> {
                return PointF(
                    MODULE_WIDTH + PADDING_1,
                    lowHeight
                )
            }
            6 -> {
                return PointF(
                    MODULE_WIDTH * 2 + (PADDING_1 + PADDING_2),
                    lowHeight
                )
            }
            7 -> {
                return PointF(
                    MODULE_WIDTH * (index - 4) + PADDING_1 * 2 + PADDING_2,
                    lowHeight
                )
            }
            else -> PointF(0F, 0F)
        }
    }
}