package com.example.laba3.data.repository

import android.util.Log
import com.example.laba3.R
import com.example.laba3.data.api.RetrofitInstance
import com.example.laba3.model.Currency
import okio.IOException

class Repository {

    private val TAG = "Repository"

    suspend fun getCurrency(): Result<Currency>
    {
        Log.d(TAG, "getCurrency() called")
        return try {
            val response = RetrofitInstance.api.getCurrency()
            Log.d(TAG, "Response is: $response")

            if(response.isSuccessful)
            {
                val body = response.body()
                if(body != null)
                {
                    Log.d(TAG, "Result success")
                    Result.Success(body)
                }
                else
                {
                    Log.d(TAG, "Result unknown error")
                    Result.UnknownError(R.string.empty_body_error.toString())
                }
            }
            else
            {
                Log.d(TAG, "Result http error, код ошибки: ${response.code()}")
                val errorMessage = response.errorBody()?.string() ?: R.string.unknown_error.toString()
                Result.HttpError(response.code(), errorMessage)
            }
        }
        catch (e: IOException)
        {
            Log.d(TAG, "Ошибка сети: ${e.localizedMessage}")
            Result.NetworkError(R.string.network_error.toString() + e.localizedMessage)
        }
        catch (e: Exception)
        {
            Log.d(TAG, "Непредвиденная ошибка: ${e.localizedMessage}")
            Result.UnknownError(R.string.unexpected_error.toString() + e.localizedMessage)
        }
    }
}