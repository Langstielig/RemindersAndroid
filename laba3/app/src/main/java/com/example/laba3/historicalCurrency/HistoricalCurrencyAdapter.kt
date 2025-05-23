package com.example.laba3.historicalCurrency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3.R
import com.example.laba3.model.AverageRatesLast5Day
import java.text.SimpleDateFormat
import java.util.Locale

class HistoricalCurrencyAdapter:RecyclerView.Adapter<HistoricalCurrencyAdapter.HistoricalCurrencyViewHolder> (){
    private var historicalCurrencyList = emptyList<AverageRatesLast5Day>()

    class HistoricalCurrencyViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricalCurrencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.historical_currency_item, parent, false)
        return HistoricalCurrencyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historicalCurrencyList.size
    }

    override fun onBindViewHolder(holder: HistoricalCurrencyViewHolder, position: Int) {
        val item = historicalCurrencyList[position]

        holder.itemView.findViewById<TextView>(R.id.historical_currency_date).text = fixDateFormat(item.date)
        holder.itemView.findViewById<TextView>(R.id.historical_currency_rate).text = item.rate.toString()
    }

    fun setList(list: List<AverageRatesLast5Day>)
    {
        historicalCurrencyList = list
        notifyDataSetChanged()
    }

    fun fixDateFormat(date: String): String
    {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val result = inputFormat.parse(date)
        return outputFormat.format(result!!)
    }
}