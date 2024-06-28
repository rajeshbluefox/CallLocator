package com.familylocation.mobiletracker.zDatabase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class BlockedContactsDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "BlockedNumbers.db"
        private const val TABLE_NAME = "BlockedNumbers"
        private const val COLUMN_PHONE_NUMBER = "PhoneNumber"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_PHONE_NUMBER TEXT PRIMARY KEY)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertNumber(phoneNumber: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_PHONE_NUMBER, phoneNumber)
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L // Insert returns -1 if it fails
    }

    fun deleteNumber(phoneNumber: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_PHONE_NUMBER = ?", arrayOf(phoneNumber))
        return result > 0 // Delete returns the number of rows affected
    }

    fun findNumber(phoneNumber: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_PHONE_NUMBER = ?", arrayOf(phoneNumber))
        val exists = cursor?.count ?: 0 > 0
        cursor?.close()
        return exists
    }

    fun getAllNumbers(): List<String> {
        val numbersList = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_PHONE_NUMBER FROM $TABLE_NAME", null)
        cursor.use {
            while (it.moveToNext()) {
                val phoneNumber = it.getString(it.getColumnIndex(COLUMN_PHONE_NUMBER))
                numbersList.add(phoneNumber)
            }
        }
        return numbersList
    }

}