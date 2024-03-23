package realapps.live.callerlocator.setThemeModule

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import realapps.live.callerlocator.callThemesModule.supportFunctions.SelectedTheme
import realapps.live.callerlocator.databinding.ActivitySelectContactsBinding
import realapps.live.callerlocator.setThemeModule.supportFunctions.ContactDatabaseHelper
import realapps.live.callerlocator.setThemeModule.supportFunctions.OtherContactsAdapter
import realapps.live.callerlocator.setThemeModule.supportFunctions.SelectContactsAdapter
import realapps.live.callerlocator.zCommonFuntions.Contact
import realapps.live.callerlocator.zCommonFuntions.ContactManager
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions

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

        onClickListeners()
        coroutineScope.launch {
            showPB()
            getContacts()
        }
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

        initOtherContacts()
        initSelectedContacts()
    }


    private var otherContacts: ArrayList<Contact> = ArrayList()
    private lateinit var otherContactsAdapter: OtherContactsAdapter

    private fun initOtherContacts() {

        otherContactsAdapter = OtherContactsAdapter(
            this,
            otherContacts,
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

        Log.e("Test","111")

    }

    private fun onContactClicked(contact: Contact, listType: Int, position: Int) {

        if (listType == 0) {
            val insertPosition = selectedContacts.size
            selectedContacts.add(contact)
            selectedContactsAdapter.notifyItemInserted(insertPosition)
            Log.e("Test", "Added $insertPosition")

            if (position != -1) {
                otherContacts.remove(contact)
                otherContactsAdapter.notifyItemRemoved(position)
                Log.e("Test", "Item Removed $position")
            }

        } else {

            val insertPosition = otherContacts.size
            otherContacts.add(contact)
            otherContactsAdapter.notifyItemInserted(insertPosition)


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

        Log.e("Test","166")
        hidePB()
    }


    private fun addContact(contact: Contact) {
        dbHelper.addContact(contact)
    }

    private fun getContactFromDB() {

        Log.e("Test", "174")
        var allDBContacts = dbHelper.getAllContacts()

        for (contact in allContacts) {
            val foundContact = allDBContacts.find { it.number == contact.number }
            if (foundContact == null)
                addContact(contact)
        }

        Log.e("Test", "183")

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

        Log.e("Test", "201")
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
//        getContactFromDB()
//        finish()
    }

}