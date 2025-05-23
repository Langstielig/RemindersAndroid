package com.example.laba3.model

data class AverageRatesLast5Day(
    val date: String,
    val rate: Double
) {
    override fun toString(): String {
        return "date: $date, rate: $rate"
    }
}