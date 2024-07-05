package com.familylocation.mobiletracker.callSettingsModule.supportFunctions

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.familylocation.mobiletracker.databinding.ItemBlockNumberBinding
import com.familylocation.mobiletracker.zCommonFuntions.UtilFunctions

class BlockNumberDialog(
    layoutInflater: LayoutInflater,
    context: Context,
    private val listener: (phoneNumber: String) -> Unit
) {

    private val mLayoutInflater: LayoutInflater
    private val mContext: Context

    private var binding: ItemBlockNumberBinding
    private var dialog: AlertDialog.Builder
    private var messageBoxInstance: AlertDialog

    init {
        mLayoutInflater = layoutInflater
        mContext = context
        binding = ItemBlockNumberBinding.inflate(mLayoutInflater)
        dialog = AlertDialog.Builder(mContext).setView(binding.root)
        dialog.setCancelable(true)
        messageBoxInstance = dialog.create()

    }


    fun openBlockNumbersDialog() {

        binding.btAddBlockNumber.setOnClickListener {
            val mPhoneNumber = binding.etSearch.text.toString()

            if(mPhoneNumber.length<10)
            {
                UtilFunctions.showToast(context = mContext,"Enter Valid Number")
            }else{
                binding.etSearch.setText("")
                listener.invoke(mPhoneNumber)
//                messageBoxInstance.dismiss()

            }
        }

        messageBoxInstance.show()

    }

    fun close()
    {
        messageBoxInstance.dismiss()

    }

}