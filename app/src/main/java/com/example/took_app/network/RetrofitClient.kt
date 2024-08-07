package com.example.took_app.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.IOException
import okhttp3.Request
import okhttp3.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://i11e205.p.ssafy.io"
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhIiwiaWF0IjoxNzIzMDE4NzE5LCJleHAiOjE3MjMwMjIzMTl9.jWLMUZgJY1KrvvThPnFSaYRpbaPmKR3GWcv_kZ9gJy4") // 여기에 JWT 토큰을 하드코딩합니다.
                val request: Request = requestBuilder.build()
                return chain.proceed(request)
            }
        }).build()
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()) // JSON 응답을 처리하기 위해 GsonConverterFactory 사용
            .build()
    }
}
