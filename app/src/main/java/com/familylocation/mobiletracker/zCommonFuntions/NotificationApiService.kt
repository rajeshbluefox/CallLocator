package com.familylocation.mobiletracker.zCommonFuntions

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApiService {
    @POST("fcm/send")
    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAAcgjHxXQ:APA91bHfxDpxdGaaekNM9NT6hNjeC7RFerOAzSXicFVimfZhnljR5OnKXrmtBschIdQp3v7z52pgTW9IadZH3O-atIlXQb59Sga-n85T0wXB-s2cZ4LElQzXW9lJnwLklCogBjUuF7Y8"
    )
    fun sendNotification(@Body requestBody: RequestBody): Call<ApiResponse>
}