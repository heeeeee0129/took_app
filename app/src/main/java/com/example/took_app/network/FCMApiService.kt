package com.example.took_app.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FCMApiService {
    @POST("api/fcm/token")
    fun saveToken(@Body request: FCMTokenRequest): Call<String>
}

data class FCMTokenRequest(val userSeq: Long, val token: String)
