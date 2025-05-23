package com.example.laba3.currentCurrency

import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3.R
import com.example.laba3.model.Rate
import com.example.laba3.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrentCurrencyFragment : Fragment(), OnItemClickListener {
    private val TAG = "CurrentCurrencyFragment"

    lateinit var recyclerView: RecyclerView
    lateinit var baseCurrencyTitle: TextView
    lateinit var adapter: CurrencyAdapter
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = ViewModelProvider(this)[CurrentCurrencyViewModel::class.java]

        val v = inflater.inflate(R.layout.fragment_current_currency, container, false)

        navController = findNavController()

        baseCurrencyTitle = v.findViewById(R.id.base_currency_title)

        setRecyclerViewAdapter(v)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true)
                {
                    viewModel.getCurrentCurrency()
                    Log.d(TAG, "Обновление данных")
                    delay(60_000)
                }
            }
        }

        viewModel.rates.observe(viewLifecycleOwner) {  rates ->
            adapter.setList(rates)
        }
        viewModel.baseCurrency.observe(viewLifecycleOwner) { base ->
            baseCurrencyTitle.text = getString(R.string.base_currency, base)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Utils.showSnackbar(requireView(), it)
            }
        }

        return v
    }

    override fun onItemClick(item: Rate) {
        val action = CurrentCurrencyFragmentDirections
            .actionCurrentCurrencyFragmentToHistoricalCurrencyFragment(item.currency)

        navController.navigate(action)
    }

    private fun setRecyclerViewAdapter(view: View)
    {
        recyclerView = view.findViewById(R.id.rv_current_currency)
        adapter = CurrencyAdapter(this)
        recyclerView.adapter = adapter
    }
}