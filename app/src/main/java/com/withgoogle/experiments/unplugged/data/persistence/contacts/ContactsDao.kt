package com.withgoogle.experiments.unplugged.data.persistence.contacts

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.withgoogle.experiments.unplugged.model.Contact

@Dao
interface ContactsDao {
    @Query("SELECT * from contacts ORDER BY fullName ASC")
    fun getContacts(): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun deleteContact(contactId: Long)
}