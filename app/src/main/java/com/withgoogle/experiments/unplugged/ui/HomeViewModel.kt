package com.withgoogle.experiments.unplugged.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.withgoogle.experiments.unplugged.data.persistence.AppDatabase
import com.withgoogle.experiments.unplugged.data.integrations.contacts.ContactsDataSource
import com.withgoogle.experiments.unplugged.model.Contact

class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val dataSource: ContactsDataSource

    val allContacts: LiveData<List<Contact>>

    init {
        val contactsDao = AppDatabase.getInstance(application).contactsDao()
        dataSource = ContactsDataSource(contactsDao)
        allContacts = dataSource.contacts
    }
}