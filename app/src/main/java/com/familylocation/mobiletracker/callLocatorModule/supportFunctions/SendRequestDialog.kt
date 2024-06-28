package com.familylocation.mobiletracker.callLocatorModule.supportFunctions

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.familylocation.mobiletracker.databinding.AlertSendRequestBinding


class SendRequestDialog(
    layoutInflater: LayoutInflater,
    context: Context,
    private val onSendRequestClicked: () -> Unit

) {
    private val mLayoutInflater: LayoutInflater
    private val mContext: Context

    private var binding: AlertSendRequestBinding
    private var dialog: AlertDialog.Builder
    private var messageBoxInstance: AlertDialog

    init {
        mLayoutInflater = layoutInflater
        mContext = context
        binding = AlertSendRequestBinding.inflate(mLayoutInflater)
        dialog = AlertDialog.Builder(mContext).setView(binding.root)
        dialog.setCancelable(true)
        messageBoxInstance = dialog.create()
    }

    fun openSendRequestSheet() {
        messageBoxInstance.show()

        binding.btSendRequest.setOnClickListener {

            onSendRequestClicked.invoke()
//            closeSendRequestSheet()
        }

        binding.btCancel.setOnClickListener {
            closeSendRequestSheet()
        }
    }

    private fun closeSendRequestSheet() {
        messageBoxInstance.dismiss()
    }


}