package com.withgoogle.experiments.unplugged.ui.contacts

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.model.Contact
import com.withgoogle.experiments.unplugged.ui.PdfModule

class ContactsModule(val contacts: List<Contact>): PdfModule {
    override val isRotated: Boolean
        get() = true

    override fun draw(canvas: Canvas, resources: Resources) {
        val titleTextPaint = Paint().apply {
            textSize = 11.33F
            color = Color.BLACK
            letterSpacing = 0.1F
            typeface = resources.getFont(R.font.varela_regular)
        }

        val fm = titleTextPaint.fontMetrics
        val height = fm.descent - fm.ascent

        canvas.drawText("CONTACTS", 0F, height, titleTextPaint)

        val entriesTextPaint = Paint().apply {
            textSize = 7.31F
            color = Color.BLACK
            letterSpacing = 0.12F
            typeface = resources.getFont(R.font.worksans_regular)
        }

        val fontMetrics = entriesTextPaint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent

        for(contact: Contact in contacts) {
            with(canvas) {
                translate(0F, 24F)
                drawText(contact.fullName.toUpperCase(), 0F, fontHeight, entriesTextPaint)

                translate(0F, 8F)
                drawText(contact.phoneNumber, 0F, fontHeight, entriesTextPaint)
            }
        }
    }
}