package realapps.live.callerlocator.callThemesModule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import realapps.live.callerlocator.R
import realapps.live.callerlocator.callThemesModule.supportFunctions.CallerThemesAdapter
import realapps.live.callerlocator.callThemesModule.supportFunctions.CallerThemesUI
import realapps.live.callerlocator.callThemesModule.supportFunctions.SpacesItemDecoration
import realapps.live.callerlocator.databinding.FragmentCallThemesBinding
import realapps.live.callerlocator.setThemeModule.supportFunctions.SelectContactsAdapter
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zCommonFuntions.Contact
import realapps.live.callerlocator.zCommonFuntions.ContactManager
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils


class CallThemesFragment : Fragment() {

    private lateinit var binding: FragmentCallThemesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_themes, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initViews()
        getContacts()
        onClickListeners()
    }

    private lateinit var callerThemesUI: CallerThemesUI

    private fun initViews()
    {
        callerThemesUI = CallerThemesUI(requireContext(),binding)
//        callerThemesUI.setTopPadding(resources)
    }

    private fun onClickListeners() {

    }

    private var allContacts: ArrayList<Contact> = ArrayList()

    private fun getContacts() {
        Log.e("Test", "Reading contacts")

        val contactManager = ContactManager(requireContext())
        val contacts = contactManager.getContacts()

        allContacts = contacts as ArrayList<Contact>

        initThemes()

    }

    private var themes = ArrayList<Int>()

    fun setThemes()
    {
        themes.add(R.raw.call_theme_1)
        themes.add(R.raw.call_theme_2)
        themes.add(R.raw.call_theme_3)
        themes.add(R.raw.call_theme_4)
    }

    private fun initThemes() {

        setThemes()

        val callerThemesAdapter = CallerThemesAdapter(
            requireContext(),
            allContacts,
            themes,
            ::onThemeSelected
        )

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)

        binding.rvThemes.apply {
            layoutManager = GridLayoutManager(
                context,
                3,  // Number of items in each row
                LinearLayoutManager.VERTICAL,
                false
            )
            addItemDecoration(SpacesItemDecoration(spacingInPixels,3))
            adapter = callerThemesAdapter
        }

    }

    private fun onThemeSelected(contact: Contact) {
        CallIntent.goToSetThemeActivity(requireContext(), false, requireActivity())
    }

}