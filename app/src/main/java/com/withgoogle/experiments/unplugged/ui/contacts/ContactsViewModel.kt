package com.withgoogle.experiments.unplugged.ui.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.withgoogle.experiments.unplugged.data.persistence.AppDatabase
import com.withgoogle.experiments.unplugged.data.integrations.contacts.ContactsDataSource
import com.withgoogle.experiments.unplugged.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application): AndroidViewModel(application) {
    private val dataSource: ContactsDataSource

    val allContacts: LiveData<List<Contact>>

    init {
        val contactsDao = AppDatabase.getInstance(application).contactsDao()
        dataSource = ContactsDataSource(contactsDao)
        allContacts = dataSource.contacts
    }

    fun insert(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        dataSource.insert(contact)
    }

    fun delete(contactId: Long) = viewModelScope.launch(Dispatchers.IO) {
        dataSource.delete(contactId)
    }
}