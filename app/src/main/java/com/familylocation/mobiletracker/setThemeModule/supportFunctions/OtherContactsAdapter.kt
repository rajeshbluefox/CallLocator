package com.familylocation.mobiletracker.setThemeModule.supportFunctions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.familylocation.mobiletracker.databinding.ItemContactBinding
import com.familylocation.mobiletracker.zCommonFuntions.Contact


class OtherContactsAdapter(
    private var context: Context,
    private var contactList: ArrayList<Contact>,
    private var listType: Int,
    private val onContactClicked: (contact: Contact, listType: Int, position: Int) -> Unit,
) :
    RecyclerView.Adapter<OtherContactsAdapter.OtherContactsAdapterViewHolder>() {


    class OtherContactsAdapterViewHolder(var binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherContactsAdapterViewHolder {
        return OtherContactsAdapterViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OtherContactsAdapterViewHolder, position: Int) {

        val contact = contactList[position]

        holder.binding.tvPhoneNumber.text = contact.name
        holder.binding.tvLocationName.text = contact.number


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