package com.withgoogle.experiments.unplugged.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindableAdapter<T, VH: BindableAdapter.ViewHolder>: RecyclerView.Adapter<VH>() {

    val items: MutableList<T> = mutableListOf()

    open fun changeData(items: List<T>) {
        changeData(items, true)
    }

    fun changeData(items: List<T>, refresh: Boolean) {
        this.items.clear()
        this.items.addAll(items)

        if (refresh) refreshData()
    }

    fun updateItem(item: T, newItem: T) {
        val position = items.indexOf(item)

        items.removeAt(position)

        items.add(position, newItem)

        notifyDataSetChanged()
    }

    fun refreshData() {
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    operator fun get(position: Int): T = items[position]

    override fun getItemCount(): Int = items.size

    open class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}