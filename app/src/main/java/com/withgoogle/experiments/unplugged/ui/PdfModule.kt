package com.withgoogle.experiments.unplugged.ui

import android.content.res.Resources
import android.graphics.Canvas

interface PdfModule {
    fun draw(canvas: Canvas, resources: Resources)
    val isRotated: Boolean
}