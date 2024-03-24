package realapps.live.callerlocator.callThemesModule.supportFunctions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.journeyapps.barcodescanner.Util
import realapps.live.callerlocator.zCommonFuntions.UtilFunctions


class CallEndedReceiver() : BroadcastReceiver() {
    private lateinit  var onResponseReceived: () -> Unit


    fun initCallBack(onResponseReceivedP: () -> Unit )
    {
        onResponseReceived=onResponseReceivedP
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "ACTION_CALL_ENDED") {
//            UtilFunctions.showToast(context!!,"Call Ended")
            onResponseReceived.invoke()
            // Call ended, handle it here
            // For example, you can perform actions or start services
        }
    }
}