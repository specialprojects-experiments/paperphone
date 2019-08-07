package com.withgoogle.experiments.unplugged.data.integrations.contacts

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.withgoogle.experiments.unplugged.model.Account
import com.withgoogle.experiments.unplugged.model.Contact

class ContactsImporter(val context: Context) {
    private val CONTACTS_PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone._ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
    )

    fun forAccount(account: Account): List<Contact> {
        val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val cursor = context.contentResolver.query(uri, CONTACTS_PROJECTION, null, null, null)

        cursor?.use {
            return if (cursor.count > 0) {
                generateSequence { if (cursor.moveToNext()) cursor else null }
                    .map { cursorToContact(cursor) }
                    .toList()
            } else {
                emptyList()
            }
        }

        return emptyList()
    }

    fun forUri(uri: Uri): Contact? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            it.moveToFirst()

            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

            val index = cursor.getLong(idIndex)
            val name = cursor.getString(nameIndex)
            val phoneNo = cursor.getString(phoneIndex)

            return Contact(index, name, phoneNo)
        }

        return null
    }

    private fun cursorToContact(cursor: Cursor): Contact {
        return Contact(
            id = cursor.getLong(0),
            fullName = cursor.getString(1),
            phoneNumber = cursor.getString(2)
        )
    }
}

