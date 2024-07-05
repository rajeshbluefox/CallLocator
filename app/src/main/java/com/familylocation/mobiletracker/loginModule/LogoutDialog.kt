package com.familylocation.mobiletracker.loginModule

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.familylocation.mobiletracker.R


class LogoutDialog(
    layoutInflater: LayoutInflater,
    context: Context,
    private val listener: () -> Unit
) {

    private val mLayoutInflater: LayoutInflater
    private val mContext: Context

    private lateinit var dialog: AlertDialog.Builder
    private lateinit var messageBoxInstance: AlertDialog

    private lateinit var tvMainTitle : TextView
    private lateinit var btYes : TextView
    private lateinit var btNo : TextView

    init {
        mLayoutInflater = layoutInflater
        mContext = context

        initViews()
    }


    fun initViews() {
        val view = mLayoutInflater.inflate(R.layout.alert_logout, null)
        dialog = AlertDialog.Builder(mContext,R.style.CurvedDialog).setView(view)
        dialog.setCancelable(false)
        messageBoxInstance = dialog.create()

         btYes = view.findViewById(R.id.btYes)
         btNo = view.findViewById(R.id.btNo)

        tvMainTitle=view.findViewById(R.id.mainTitle)


    }

    fun openLogoutDialog() {


        btNo.setOnClickListener {
            messageBoxInstance.dismiss()
        }

        btYes.setOnClickListener {
            listener.invoke()
            messageBoxInstance.dismiss()
        }

        messageBoxInstance.show()

    }

}