package com.example.laba3.data.api

import com.example.laba3.model.Currency
import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET(Constants.ENDPOINT)
    //@GET("wrong-endpoint")
    suspend fun getCurrency(): Response<Currency>
}