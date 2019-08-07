package com.withgoogle.experiments.unplugged.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.withgoogle.experiments.unplugged.model.Contact
import com.withgoogle.experiments.unplugged.ui.adapters.BindableAdapter

class ContactsSelectorAdapter: BindableAdapter<Contact, BindableAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)

        return LevelViewHolder(inflater.inflate(android.R.layout.simple_list_item_checked, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder as LevelViewHolder
        holder.bind(this[position])
    }

    inner class LevelViewHolder(itemView: View) : BindableAdapter.ViewHolder(itemView) {
        fun bind(contact: Contact) = with(itemView) {
            val title = findViewById<TextView>(android.R.id.text1)

            title.text = "${contact.fullName} - ${contact.phoneNumber}"
        }
    }
}