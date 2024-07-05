package com.familylocation.mobiletracker.zCommonFuntions

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract

class ContactManager(private val context: Context) {

    fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        // Projection for the columns you want to retrieve
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // Query the contacts
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val nameColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameColumnIndex)
                val number = it.getString(numberColumnIndex)

                val contact = Contact(name, number)
                contacts.add(contact)
            }
        }

        return contacts
    }
}

data class Contact(val name: String, var number: String, val themeSelected: Int = 0)

