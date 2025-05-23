package com.example.laba3.model

data class Rate(
    val averageRatesLast5Days: List<AverageRatesLast5Day>,
    val currency: String,
    val currentRate: Double
)