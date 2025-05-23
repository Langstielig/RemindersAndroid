package com.example.laba3.model

data class Currency(
    val baseCurrency: String,
    val rates: List<Rate>
)
{
    override fun toString(): String {
        return "base currency: $baseCurrency, rates: $rates"
    }
}