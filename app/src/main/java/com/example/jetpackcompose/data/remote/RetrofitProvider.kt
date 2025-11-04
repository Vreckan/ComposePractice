package com.example.jetpackcompose.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)  // ★ 連線等待
        .readTimeout(60, TimeUnit.SECONDS)     // ★ 讀取等待
        .writeTimeout(60, TimeUnit.SECONDS)    // ★ 上傳等待
        .build()

    fun openai(): OpenAiApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(OpenAiApi::class.java)
    }
}