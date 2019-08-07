package com.withgoogle.experiments.unplugged.util

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

fun <T : View> View.bindView(id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) ?: viewNotFound(id) }

fun <T : View> Activity.bindView(id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) ?: viewNotFound(id) }

fun <T : View> Fragment.bindView(id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { view?.findViewById<T>(id) ?: viewNotFound(id) }

fun <T : View> RecyclerView.ViewHolder.bindView(id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { itemView.findViewById<T>(id) ?: viewNotFound(id) }

private fun viewNotFound(id: Int): Nothing =
    throw IllegalStateException("View ID $id not found.")
