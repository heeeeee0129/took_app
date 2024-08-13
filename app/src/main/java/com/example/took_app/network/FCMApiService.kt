package com.example.took_app.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FCMApiService {
    @POST("api/fcm/token")
    suspend fun saveToken(@Body request: FCMTokenRequest): Response<String>
}

data class FCMTokenRequest(val userSeq: Long, val token: String)
