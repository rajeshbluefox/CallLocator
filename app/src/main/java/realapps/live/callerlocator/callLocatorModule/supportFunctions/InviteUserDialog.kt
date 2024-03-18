package realapps.live.callerlocator.callLocatorModule.supportFunctions

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import realapps.live.callerlocator.databinding.AlertInviteUserBinding

class InviteUserDialog(
    layoutInflater: LayoutInflater,
    context: Context,
) {
    private val mLayoutInflater: LayoutInflater
    private val mContext: Context

    private var binding: AlertInviteUserBinding
    private var dialog: AlertDialog.Builder
    private var messageBoxInstance: AlertDialog

    init {
        mLayoutInflater = layoutInflater
        mContext = context
        binding = AlertInviteUserBinding.inflate(mLayoutInflater)
        dialog = AlertDialog.Builder(mContext).setView(binding.root)
        dialog.setCancelable(true)
        messageBoxInstance = dialog.create()
    }

    fun openInviteUserSheet() {
        messageBoxInstance.show()

        binding.btSendInvitation.setOnClickListener {
            closeInviteUserSheet()
            shareAppLink()
        }
    }

    private fun closeInviteUserSheet() {
        messageBoxInstance.dismiss()
    }

    private fun shareAppLink()
    {
        // Your recommendation text
        val shareText = "Hey, I recommend using this app to share location: https://play.google.com/store/apps/details?id=com.mobile.number.locator.phone.gps.map"

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        mContext.startActivity(Intent.createChooser(shareIntent, "Share using"))
    }
}