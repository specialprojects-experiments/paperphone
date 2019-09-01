package com.withgoogle.experiments.unplugged.ui

import android.content.res.Resources
import android.graphics.Canvas

interface PdfModule {
    /**
     * Drawing step, runs on the UI thread
     */
    fun draw(canvas: Canvas, resources: Resources)

    /**
     * Attribute to draw this module upside down
     */
    val isRotated: Boolean

    /**
     * Fetch data step. This is run on a background
     */
    suspend fun setupData()
}