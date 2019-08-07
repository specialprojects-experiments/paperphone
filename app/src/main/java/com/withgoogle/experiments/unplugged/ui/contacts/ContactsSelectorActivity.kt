package com.withgoogle.experiments.unplugged.ui.contacts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.contacts.ContactsImporter
import com.withgoogle.experiments.unplugged.ui.AppState

class ContactsSelectorActivity: AppCompatActivity() {
    private val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recycler_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_selector)
        setupRecyclerView()

        loadContacts()
    }

    private val contactsAdapter = ContactsSelectorAdapter()

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactsAdapter
    }

    private fun loadContacts() {
        AppState.account.observe(this, Observer { account ->
            val contacts = ContactsImporter(this).forAccount(account)

            contactsAdapter.changeData(contacts)
        })
    }
}