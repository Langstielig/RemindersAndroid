package com.example.laba3.currentCurrency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laba3.R
import com.example.laba3.model.Rate

class CurrencyAdapter(
    private val listener: OnItemClickListener
):RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private var currentCurrencyList = emptyList<Rate>()

    class CurrencyViewHolder(view: View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currentCurrencyList.size
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val item = currentCurrencyList[position]

        holder.itemView.findViewById<TextView>(R.id.currency_title).text = item.currency

        val iconId = chooseIcon(item.currency)
        holder.itemView.findViewById<ImageView>(R.id.currency_icon).setImageResource(iconId)

        holder.itemView.findViewById<TextView>(R.id.currency_current_rate).text = item.currentRate.toString()

        holder.itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    fun setList(list: List<Rate>)
    {
        currentCurrencyList = list
        notifyDataSetChanged()
    }

    fun chooseIcon(currencyName: String): Int
    {
        val result = when(currencyName)
        {
            "CAD" -> R.drawable.currency_cad
            "CHF" -> R.drawable.currency_chf
            "CNY" -> R.drawable.currency_cny
            "EUR" -> R.drawable.currency_eur
            "GBP" -> R.drawable.currency_gbp
            "JPY" -> R.drawable.currency_jpy
            "AUD" -> R.drawable.currency_aud
            else -> { R.drawable.currency_usd }
        }
        return result
    }
}