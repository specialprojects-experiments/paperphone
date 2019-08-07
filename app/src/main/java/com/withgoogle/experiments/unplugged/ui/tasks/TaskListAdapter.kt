package com.withgoogle.experiments.unplugged.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.model.TaskList
import com.withgoogle.experiments.unplugged.ui.adapters.BindableAdapter
import com.withgoogle.experiments.unplugged.util.bindView

class TaskListAdapter(val itemClickListener: ((item: TaskList, checked: Boolean) -> Unit)):
    BindableAdapter<TaskListModel, BindableAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)

        return TaskListViewHolder(inflater.inflate(R.layout.list_item_2, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder as TaskListViewHolder
        holder.bind(this[position])
    }

    inner class TaskListViewHolder(itemView: View) : BindableAdapter.ViewHolder(itemView) {
        private val taskTitle by bindView<TextView>(R.id.first)
        private val numberOfTasks by bindView<TextView>(R.id.second)
        private val checkBox by bindView<CheckBox>(R.id.check)

        fun bind(item: TaskListModel) = with(itemView) {
            taskTitle.text = item.taskList.name
            numberOfTasks.text = "${item.taskList.taskItems.size} tasks"
            checkBox.isChecked = item.checked

            setOnClickListener {
                item.checked = !checkBox.isChecked

                itemClickListener.invoke(item.taskList, item.checked)

                notifyDataSetChanged()
            }
        }
    }
}