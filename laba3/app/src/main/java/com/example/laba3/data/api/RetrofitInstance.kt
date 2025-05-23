package com.example.laba3.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            //.baseUrl(Constants.BASE_URL)
            .baseUrl("https://mej1g.wiremockapi.cloud/wrong-path/")
            //.baseUrl("https://mej1g.wiremockapi.cloud/1")
            //.baseUrl("https://mej1g.wirehmockapi.cloud/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val api: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}