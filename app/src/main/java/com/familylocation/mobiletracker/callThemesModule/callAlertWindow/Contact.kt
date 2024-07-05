package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils

class Contact2(phoneNumber: String) {

    private val context: Context = App.getContext()
    var type: Number = Number.HIDDEN
    var number: String? = null
    var name: String? = null
    var company: String? = null
    var companyPosition: String? = null
    var photo: Bitmap? = null

    init {
        try {
            setNumber1(phoneNumber)
            type = Number.JUST_PHONE
            setContact(phoneNumber)
            type = Number.FULL
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setNumber1(phoneNumber: String) {
        val numberInt = try {
            phoneNumber.toLong()
        } catch (e: Exception) {
            -1L
        }

        if (numberInt < 0) {
            throw Exception("Hidden number")
        }

        number = PhoneNumberUtils.formatNumber(phoneNumber, "US") ?: phoneNumber
    }

    private fun setContact(phoneNumber: String) {
        val contentResolver: ContentResolver = context.contentResolver
        val phoneUri: Uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        val contactCursor: Cursor? = contentResolver.query(phoneUri, null, null, null, null)
        contactCursor?.use {
            if (it.moveToFirst()) {
                val contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                val orgCursor: Cursor? = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
                    null
                )
                orgCursor?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY))
                        companyPosition = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE))
                    }
                }

                val contactUri: Uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
                val displayPhotoUri: Uri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO)
                try {
                    val photoFd: AssetFileDescriptor? = contentResolver.openAssetFileDescriptor(displayPhotoUri, "r")
                    photoFd?.use { fd ->
                        photo = BitmapFactory.decodeStream(fd.createInputStream())
                    }
                } catch (e: Exception) {
                    photo = null
                }
            }
        }
    }

    enum class Number {
        HIDDEN, JUST_PHONE, FULL
    }
}
