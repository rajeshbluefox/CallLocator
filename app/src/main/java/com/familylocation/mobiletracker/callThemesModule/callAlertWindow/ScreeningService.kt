package com.familylocation.mobiletracker.callThemesModule.callAlertWindow

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection
import android.util.Log


class ScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        // Check if the call is incoming
        val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        if (isIncoming) {
            // Get the handle (phone number) of the incoming call
            val handle: Uri? = callDetails.handle

            // Extract the phone number from the handle Uri
            val incomingNumber = handle?.schemeSpecificPart

            if (incomingNumber != null) {
                // Do something with the incoming number
                // For example, log it or send it to another service
                // Here, just log it
                Log.e("Test","Incoming call from: $incomingNumber")
                println("Incoming call from: $incomingNumber")

                // Determine if you want to allow or reject the call based on verification status
                when (callDetails.callerNumberVerificationStatus) {
                    Connection.VERIFICATION_STATUS_FAILED -> {
                        // Network verification failed, likely an invalid/spam call.
                        respondToCall(callDetails, CallResponse.Builder()
                            .setDisallowCall(true)
                            .setRejectCall(true)
                            .setSkipCallLog(true)
                            .setSkipNotification(true)
                            .build())
                    }
                    Connection.VERIFICATION_STATUS_PASSED -> {
                        // Network verification passed, likely a valid call.
                        respondToCall(callDetails, CallResponse.Builder()
                            .setDisallowCall(false)
                            .build())
                    }
                    else -> {
                        // Network could not perform verification.
                        // This branch matches Connection.VERIFICATION_STATUS_NOT_VERIFIED.
                        respondToCall(callDetails, CallResponse.Builder()
                            .setDisallowCall(false)
                            .build())
                    }
                }
            }
        }
    }
}
