package com.withgoogle.experiments.unplugged.data.integrations.contacts

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.withgoogle.experiments.unplugged.data.persistence.contacts.ContactsDao
import com.withgoogle.experiments.unplugged.model.Contact

class ContactsDataSource(private val contactsDao: ContactsDao) {
    val contacts: LiveData<List<Contact>> = contactsDao.getContacts()

    @WorkerThread
    suspend fun insert(contact: Contact) {
        contactsDao.insert(contact)
    }

    @WorkerThread
    suspend fun delete(contactId: Long) {
        contactsDao.deleteContact(contactId)
    }
}