package com.example.laba3.currentCurrency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laba3.data.repository.Repository
import com.example.laba3.model.Rate
import kotlinx.coroutines.launch
import com.example.laba3.data.repository.Result

class CurrentCurrencyViewModel: ViewModel() {
    var repository = Repository()

    var rates = MutableLiveData<List<Rate>>()
    var baseCurrency = MutableLiveData<String>()
    var errorMessage = MutableLiveData<String>()

    fun getCurrentCurrency() {
        viewModelScope.launch {
            when (val result = repository.getCurrency())
            {
                is Result.Success ->
                {
                    rates.value = result.data.rates
                    baseCurrency.value = result.data.baseCurrency
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