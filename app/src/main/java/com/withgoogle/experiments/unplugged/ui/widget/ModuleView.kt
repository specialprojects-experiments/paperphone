package com.withgoogle.experiments.unplugged.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.util.bindView

class ModuleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CheckableLinearLayout(context, attrs, defStyleAttr) {

    init {
        inflateView(context)
    }

    private val button by bindView<CheckableTextView>(R.id.toggler)
    private val description by bindView<TextView>(R.id.description)

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupHandlers()
    }

    private fun setupHandlers() {
        setOnLongClickListener {
            if (!isChecked) {
                isChecked = true
            }
            longClickListener?.onLongClick(it)

            true
        }
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    var longClickListener: OnLongClickListener? = null

    fun setOnLongPress(listener: OnLongClickListener) {
        longClickListener = listener
    }

    fun setText(letter: String, title: String) {
        button.text = letter
        description.text = title
    }

    fun inflateView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.module_view, this, true)

        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
    }
}