package com.example.laba3.data.repository

sealed class Result<out T> {
    data class Success<out T>(val data: T): Result<T>()
    data class HttpError(val code: Int, val message: String): Result<Nothing>()
    data class NetworkError(val message: String): Result<Nothing>()
    data class UnknownError(val message: String): Result<Nothing>()
}