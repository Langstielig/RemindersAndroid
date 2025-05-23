package com.example.laba3.historicalCurrency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laba3.data.repository.Repository
import com.example.laba3.data.repository.Result
import com.example.laba3.model.AverageRatesLast5Day
import kotlinx.coroutines.launch

class HistoricalCurrencyViewModel: ViewModel() {
    private val TAG = "HistoricalCurrencyViewModel"

    var repository = Repository()
    var averageRates = MutableLiveData<List<AverageRatesLast5Day>>()
    var errorMessage = MutableLiveData<String>()

    fun getRates(name: String)
    {
        viewModelScope.launch {
            when(val result = repository.getCurrency())
            {
                is Result.Success ->
                {
                    result.data.rates.forEach { rate ->
                        if(rate.currency == name)
                        {
                            averageRates.value = rate.averageRatesLast5Days
                        }
                    }
                }
                is Result.HttpError ->
                {
                    errorMessage.value = "Код ошибки: ${result.code}, ${result.message}"
                }
                is Result.NetworkError ->
                {
                    errorMessage.value = result.message
                }
                is Result.UnknownError ->
                {
                    errorMessage.value = result.message
                }
            }
        }
    }
}