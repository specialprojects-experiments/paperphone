package com.withgoogle.experiments.unplugged.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CheckedTextView
import android.widget.TextView
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.model.Calendar
import com.withgoogle.experiments.unplugged.ui.adapters.BindableAdapter
import com.withgoogle.experiments.unplugged.util.bindView

class CalendarAdapter(val itemClickListener: ((item: Calendar, chcked: Boolean) -> Unit)): BindableAdapter<CalendarModel, BindableAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)

        return LevelViewHolder(inflater.inflate(R.layout.list_item_2, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder as LevelViewHolder
        holder.bind(this[position])
    }

    inner class LevelViewHolder(itemView: View) : BindableAdapter.ViewHolder(itemView) {
        private val title by bindView<TextView>(R.id.first)
        private val subTitle by bindView<TextView>(R.id.second)
        private val checkBox by bindView<CheckBox>(R.id.check)

        fun bind(item: CalendarModel) = with(itemView) {
            title.text = item.calendar.displayName
            checkBox.isChecked = item.checked
            subTitle.text = "${item.calendar.eventsCount} events"

            setOnClickListener {
                item.checked = !checkBox.isChecked

                itemClickListener.invoke(item.calendar, item.checked)

                notifyDataSetChanged()
            }
        }
    }
}