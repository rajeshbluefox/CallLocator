package com.familylocation.mobiletracker.setThemeModule

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.familylocation.mobiletracker.callThemesModule.supportFunctions.SelectedTheme
import com.familylocation.mobiletracker.databinding.ActivitySelectContactsBinding
import com.familylocation.mobiletracker.setThemeModule.supportFunctions.ContactDatabaseHelper
import com.familylocation.mobiletracker.setThemeModule.supportFunctions.OtherContactsAdapter
import com.familylocation.mobiletracker.setThemeModule.supportFunctions.SelectContactsAdapter
import com.familylocation.mobiletracker.zCommonFuntions.Contact
import com.familylocation.mobiletracker.zCommonFuntions.ContactManager
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions

@AndroidEntryPoint
class SelectContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectContactsBinding

    private var dbHelper = ContactDatabaseHelper(this)

    private val coroutineScope =
        CoroutineScope(Dispatchers.Main) // Dispatchers.Main represents the main (UI) thread


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources, binding.titleBarLayout)

        editTextListener()
        onClickListeners()
        coroutineScope.launch {
            showPB()
            getContacts()
        }
    }

    private fun editTextListener() {

        binding.etSearch.doOnTextChanged { text, _, _, _ ->

            Log.e("Test", text.toString())

            if (text!!.isEmpty()) {
                binding.btClear.visibility = View.GONE
                binding.btSearch.visibility = View.GONE

                isFilteredList = false
                initOtherContacts(otherContacts)

            } else {
                binding.btClear.visibility = View.VISIBLE
                binding.btSearch.visibility = View.VISIBLE

                filterList(text.toString())
            }

        }

        binding.btClear.setOnClickListener {
            binding.etSearch.setText("")
        }
    }

    private var filteredContacts = ArrayList<Contact>()

    private var isFilteredList = false
    private fun filterList(searchValue: String) {
        filteredContacts.clear()
//        val filteredContacts = ArrayList<Contact>()
//        val filteredContacts = otherContacts.filter { it.name.startsWith(searchValue, ignoreCase = true) }

        for (contact in otherContacts) {
            val originalName = contact.name.lowercase()
            val enteredVal = searchValue.lowercase()
            Log.e("Test", "$originalName = $enteredVal ${originalName.startsWith(enteredVal)}")

            if (originalName.startsWith(enteredVal)) {
                filteredContacts.add(contact)
            }
        }
        isFilteredList = true

        initOtherContacts(filteredContacts)
    }

    private fun onClickListeners() {

        binding.btSet.setOnClickListener {
            UpdateThemeSelected()
        }

        binding.btBack.setOnClickListener {
            finish()
        }
    }

    fun showPB() {
        binding.pb.visibility = View.VISIBLE
        binding.contentLL.visibility = View.GONE
    }

    fun hidePB() {
        binding.pb.visibility = View.GONE
        binding.contentLL.visibility = View.VISIBLE
    }


    private var allContacts: ArrayList<Contact> = ArrayList()

    private fun getContacts() {

        Log.e("Test", "Reading contacts")

        val contactManager = ContactManager(this)
        val contacts = contactManager.getContacts()

        allContacts = contacts as ArrayList<Contact>

        Log.e("Test", "82")


        getContactFromDB()

//        filteredContacts = otherContacts
        initOtherContacts(otherContacts)
        initSelectedContacts()
    }


    private var otherContacts: ArrayList<Contact> = ArrayList()
    private lateinit var otherContactsAdapter: OtherContactsAdapter

    private fun initOtherContacts(otherContactsLoc: ArrayList<Contact>) {

        val otherContactsSorted = sortList(otherContactsLoc)

        Log.e("Test", "Filtered List Size ${otherContactsLoc.size}")

        otherContactsAdapter = OtherContactsAdapter(
            this,
            otherContactsSorted,
            0,
            ::onContactClicked
        )
        binding.rvOtherContacts.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = otherContactsAdapter
        }

        Log.e("Test", "111")

    }

    fun sortList(otherContacts: ArrayList<Contact>): ArrayList<Contact> {
        otherContacts.sortWith(Comparator { contact1, contact2 ->
            contact1.name.compareTo(contact2.name)
        })


        return otherContacts
    }

    private fun onContactClicked(contact: Contact, listType: Int, position: Int) {

        if (listType == 0) {
            val insertPosition = selectedContacts.size
            selectedContacts.add(contact)
            selectedContactsAdapter.notifyItemInserted(insertPosition)
            Log.e("Test", "Added $insertPosition")

            if (position != -1) {

                if (isFilteredList) {
                    filteredContacts.remove(contact)
                    otherContactsAdapter.notifyItemRemoved(position)
                    Log.e("Test", "Item Removed $position")
                } else {
                    otherContacts.remove(contact)
                    otherContactsAdapter.notifyItemRemoved(position)
                    Log.e("Test", "Item Removed $position")
                }

            }

        } else {

            if (isFilteredList) {
                val insertPosition = filteredContacts.size
                filteredContacts.add(contact)
                otherContactsAdapter.notifyItemInserted(insertPosition)
            } else {
                val insertPosition = otherContacts.size
                otherContacts.add(contact)
                otherContactsAdapter.notifyItemInserted(insertPosition)
            }



            if (position != -1) {
                selectedContacts.remove(contact)
                selectedContactsAdapter.notifyItemRemoved(position)

                removedItems.add(contact)
            }
        }
    }

    private var selectedContacts: ArrayList<Contact> = ArrayList()
    private var removedItems: ArrayList<Contact> = ArrayList()
    private lateinit var selectedContactsAdapter: SelectContactsAdapter

    private fun initSelectedContacts() {

        selectedContactsAdapter = SelectContactsAdapter(
            this,
            selectedContacts,
            1,
            ::onContactClicked
        )
        binding.rvSelectedContacts.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            adapter = selectedContactsAdapter
        }

        Log.e("Test", "166")
        hidePB()
    }


    private fun addContact(contact: Contact) {
        dbHelper.addContact(contact)
    }

    private fun getContactFromDB() {

//        Log.e("Test", "174")
        var allDBContacts = dbHelper.getAllContacts()

        for (contact in allContacts) {
            val foundContact = allDBContacts.find { it.number == contact.number }
            if (foundContact == null)
            {
                var number=contact.number
                number = UtilFunctions.normalizePhoneNumber(number)
                number =UtilFunctions.makePhoneNumber10(number)
                contact.number=number
                addContact(contact)
            }
        }

//        Log.e("Test", "183")

        allDBContacts = dbHelper.getAllContacts()

        for (contact in allDBContacts) {
//            Log.e(
//                "Test",
//                "Name: ${contact.name}, Number: ${contact.number} , Theme:${contact.themeSelected}"
//            )

            //Filter Contacts based on Theme
            if (contact.themeSelected == SelectedTheme.selectedThemeCount) {
                selectedContacts.add(contact)
            } else {
                otherContacts.add(contact)
            }
        }

    }

    private fun UpdateThemeSelected() {
        for (contact in selectedContacts) {
            dbHelper.updateThemeForPhoneNumber(
                UtilFunctions.normalizePhoneNumber(contact.number),
                SelectedTheme.selectedThemeCount
            )
        }

        for (contact in removedItems) {
            if (contact.themeSelected == SelectedTheme.selectedThemeCount)
                dbHelper.updateThemeForPhoneNumber(
                    UtilFunctions.normalizePhoneNumber(contact.number),
                    0
                )

        }

        UtilFunctions.showToast(this, "Updated Successfully")
        finish()
    }

}