package com.example.laba3.historicalCurrency

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3.R
import com.example.laba3.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HistoricalCurrencyFragment : Fragment() {
    private val TAG = "HistoricalCurrencyFragment"

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: HistoricalCurrencyAdapter
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = ViewModelProvider(this)[HistoricalCurrencyViewModel::class.java]

        val v = inflater.inflate(R.layout.fragment_historical_currency, container, false)

        navController = findNavController()

        setRecyclerViewAdapter(v)

        val currencyTitle = v.findViewById<TextView>(R.id.historical_currency_title)
        val backButton = v.findViewById<Button>(R.id.back_button)

        val currencyName = getCurrencyNameFromFragment()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true)
                {
                    viewModel.getRates(currencyName)
                    Log.d(TAG, "Обновление данных")
                    delay(60_000)
                }
            }
        }

        viewModel.averageRates.observe(viewLifecycleOwner) { averageRates ->
            adapter.setList(averageRates)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Utils.showSnackbar(requireView(), it)
            }
        }

        currencyTitle.text = getString(R.string.historical_currency, currencyName)

        backButton.setOnClickListener {
            navController.navigate(R.id.action_historicalCurrencyFragment_to_currentCurrencyFragment)
        }

        return v
    }

    private fun setRecyclerViewAdapter(view: View)
    {
        recyclerView = view.findViewById(R.id.rv_historical_currency)
        adapter = HistoricalCurrencyAdapter()
        recyclerView.adapter = adapter
    }

    private fun getCurrencyNameFromFragment(): String
    {
        val args = HistoricalCurrencyFragmentArgs.fromBundle(requireArguments())
        return args.currencyName
    }
}