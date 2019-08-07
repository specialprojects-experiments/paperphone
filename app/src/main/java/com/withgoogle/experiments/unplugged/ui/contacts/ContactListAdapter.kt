package com.withgoogle.experiments.unplugged.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.model.Contact
import com.withgoogle.experiments.unplugged.ui.adapters.BindableAdapter
import com.withgoogle.experiments.unplugged.util.bindView

class ContactListAdapter: BindableAdapter<Contact, BindableAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)

        return LevelViewHolder(inflater.inflate(R.layout.list_item_2, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder as LevelViewHolder
        holder.bind(this[position])
    }

    inner class LevelViewHolder(itemView: View) : BindableAdapter.ViewHolder(itemView) {
        private val fullNameView by bindView<TextView>(R.id.first)
        private val phoneView by bindView<TextView>(R.id.second)
        private val checkBoxView by bindView<CheckBox>(R.id.check)

        fun bind(contact: Contact) = with(itemView) {
            checkBoxView.visibility = View.GONE

            fullNameView.text = contact.fullName
            phoneView.text = contact.phoneNumber
        }
    }
}