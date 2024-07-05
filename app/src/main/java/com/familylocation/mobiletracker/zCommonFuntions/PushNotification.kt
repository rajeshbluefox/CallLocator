package com.familylocation.mobiletracker.zCommonFuntions

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.familylocation.mobiletracker.zSharedPreference.LoginData

object PushNotification {

    fun sendNotification(fcmToken: String, title: String, body: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val priority = "high"
                val notificationData = mapOf(
                    "title" to title,
                    "body" to body,
                    "sound" to "default"
                )

                val requestBody = buildRequestBody(fcmToken, priority, notificationData)

                val response = RetrofitClient.apiService.sendNotification(requestBody).execute()

                if (response.isSuccessful) {
                    Log.e("Test","Succ")
                    val apiResponse = response.body()
                    // Handle the ApiResponse, access data, etc.
                    apiResponse?.let {
                        println("Success: ${it.success}, Message: ${it.message}")
                        it.data?.let { data ->
                            println("Notification ID: ${data.notificationId}")
                        }
                    }
                } else {
                    Log.e("Test","Fail")

                    println("API request failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    private fun buildRequestBody(token: String, priority: String, data: Map<String, Any>): RequestBody {
        val json = mapOf(
            "to" to token,
            "priority" to priority,
            "notification" to data
        ).toJsonString()

        return json.toRequestBody("application/json".toMediaType())
    }

    private fun Map<*, *>.toJsonString(): String {
        return Gson().toJson(this)
    }

     fun getAccessToken(context: Context):String {

        var fcmToken = ""

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                UtilFunctions.showToast(context,"Failed to generate token")
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("TAG", token)
            fcmToken=token.toString()

            LoginData.saveFCMToken(context,token)

            val tk="crOqxRE3TVyvPCe9IctZW9:APA91bGg4E5aMO2Q75eDi7NaJKFG7Qdmbmk29J2t-UnOObZv59Ena4826uNwR_s7VseH0ubVmJs4SkFCz4dyY7Zz2F8FkVlk_mKw3_hI1y4TPvkvhJOXVcIDm3oGcvn6JSnoVMJe1Fs7"

            sendNotification(tk,"Gunda Mart","Test")
        })


        return fcmToken
    }

}