package com.familylocation.mobiletracker.setThemeModule.supportFunctions

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.familylocation.mobiletracker.zCommonFuntions.Contact
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions

class ContactDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "ContactsDatabase"

        // Table and column names
        private const val TABLE_CONTACTS = "contacts"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE_NUMBER = "phoneNumber"
        private const val COLUMN_THEME_SELECTED = "themeSelected"
    }

    // Create the table
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE $TABLE_CONTACTS ("
                + "$COLUMN_PHONE_NUMBER TEXT PRIMARY KEY,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_THEME_SELECTED INTEGER)"
                )
        db.execSQL(CREATE_CONTACTS_TABLE)
    }

    // Upgrade the database if needed
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    fun truncateTable() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_CONTACTS")
        db.close()
    }

    // Add a contact to the database
    fun addContact(contact: Contact) {
        val values = ContentValues()
        values.put(COLUMN_PHONE_NUMBER, UtilFunctions.normalizePhoneNumber(contact.number))
        values.put(COLUMN_NAME, contact.name)
        values.put(COLUMN_THEME_SELECTED, contact.themeSelected)

        val db = this.writableDatabase
        db.insert(TABLE_CONTACTS, null, values)
        db.close()
    }

    // Get all contacts from the database
    fun getAllContacts(): ArrayList<Contact> {
        val contactList = ArrayList<Contact>()
        val query = "SELECT * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val contact = Contact(
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_THEME_SELECTED))
                )
                contactList.add(contact)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return contactList
    }

    fun updateThemeForPhoneNumber(phoneNumber: String, newThemeSelected: Int) {
        val values = ContentValues()
        values.put(COLUMN_THEME_SELECTED, newThemeSelected)

        val db = this.writableDatabase
        db.update(TABLE_CONTACTS, values, "$COLUMN_PHONE_NUMBER = ?", arrayOf(phoneNumber))
        db.close()
    }

    // Get the theme for a specific phone number
    fun getThemeForPhoneNumber(phoneNumber: String): Int {
        val db = this.readableDatabase
        val query =
            "SELECT $COLUMN_THEME_SELECTED FROM $TABLE_CONTACTS WHERE TRIM($COLUMN_PHONE_NUMBER) = ?"

        val cursor = db.rawQuery(query, arrayOf(phoneNumber))
        var themeSelected = -1 // Default theme if not found

        if (cursor.moveToFirst()) {
            themeSelected = cursor.getInt(cursor.getColumnIndex(COLUMN_THEME_SELECTED))
        }

        cursor.close()
        db.close()

        return themeSelected
    }

    fun getNameForPhoneNumber(phoneNumber: String): String {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_NAME FROM $TABLE_CONTACTS WHERE TRIM($COLUMN_PHONE_NUMBER) = ?"
        val cursor = db.rawQuery(query, arrayOf(phoneNumber))
        var name = "Unknown" // Default name if not found

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
        }

        cursor.close()
        db.close()

        return name
    }



}
