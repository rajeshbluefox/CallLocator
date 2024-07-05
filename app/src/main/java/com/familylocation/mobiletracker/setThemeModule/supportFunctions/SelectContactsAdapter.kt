package com.familylocation.mobiletracker.setThemeModule.supportFunctions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.familylocation.mobiletracker.databinding.ItemContactBinding
import com.familylocation.mobiletracker.zCommonFuntions.Contact

class SelectContactsAdapter(
    private var context: Context,
    private var contactList: ArrayList<Contact>,
    private var listType: Int,
    private val onContactClicked: (contact: Contact, listType: Int,position: Int) -> Unit,
) :
    RecyclerView.Adapter<SelectContactsAdapter.SelectContactsAdapterViewHolder>() {


    class SelectContactsAdapterViewHolder(var binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectContactsAdapterViewHolder {
        return SelectContactsAdapterViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SelectContactsAdapterViewHolder, position: Int) {

        val contact = contactList[position]

        holder.binding.tvPhoneNumber.text = contact.name
        holder.binding.tvLocationName.text = contact.number

        if (listType == 1) {
            holder.binding.btSelect.isChecked = true
        }

        holder.binding.btSelect.isEnabled = false
        holder.binding.btSelect.isClickable = false


        holder.binding.contactLayout.setOnClickListener {
            onContactClicked.invoke(contact, listType,holder.layoutPosition)
        }


    }




    override fun getItemCount(): Int {
        return contactList.size
    }

}