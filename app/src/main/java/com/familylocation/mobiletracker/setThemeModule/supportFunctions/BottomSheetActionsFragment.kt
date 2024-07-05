package com.familylocation.mobiletracker.setThemeModule.supportFunctions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.familylocation.mobiletracker.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetActionsFragment : BottomSheetDialogFragment() {

    interface BottomSheetListener {
        fun onSetAsWallpaperClicked()
        fun onSetAsLockScreenClicked()
        fun onSetAsBothClicked()
    }

    private var listener: BottomSheetListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_actions, container, false)

        view.findViewById<Button>(R.id.btnSetAsWallpaper).setOnClickListener {
            listener?.onSetAsWallpaperClicked()
            dismiss()
        }

        view.findViewById<Button>(R.id.btnSetAsLockScreen).setOnClickListener {
            listener?.onSetAsLockScreenClicked()
            dismiss()
        }

        view.findViewById<Button>(R.id.btnSetAsBoth).setOnClickListener {
            listener?.onSetAsBothClicked()
            dismiss()
        }

        return view
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }
}
