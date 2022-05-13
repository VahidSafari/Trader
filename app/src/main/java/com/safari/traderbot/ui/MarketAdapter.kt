package com.safari.traderbot.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ItemMarketBinding
import com.safari.traderbot.model.Market

class MarketAdapter: ListAdapter<Market, MarketAdapter.MarketViewHolder>(
    object : DiffUtil.ItemCallback<Market>() {
        override fun areItemsTheSame(p0: Market, p1: Market): Boolean {
            return p0.id == p1.id
        }

        override fun areContentsTheSame(p0: Market, p1: Market): Boolean {
            return p0.name == p1.name
        }

    }
) {

    lateinit var selectedMarket: Triple<Int,Int, String>

    inner class MarketViewHolder(private val binding: ItemMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var market: Market

        fun bind(market: Market) {
            this.market = market
            binding.marketName.text = market.name
            if (::selectedMarket.isInitialized && this.market.id == selectedMarket.first) {
                setItemBackgroundColor(R.color.green)
            } else {
                setItemBackgroundColor(R.color.grey)
            }
        }

        fun setClickListeners() {
            this.itemView.setOnClickListener {
                if (::selectedMarket.isInitialized) { notifyDataSetChanged() }
                setItemBackgroundColor(R.color.green)
                selectedMarket = Triple(market.id, this.layoutPosition, binding.marketName.text.toString())
            }
        }

        private fun setItemBackgroundColor(colorRes: Int) {
            binding.marketName.setBackgroundResource(colorRes)
            Log.d("setItemBackgroundColor", "${binding.marketName.text}")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MarketViewHolder {
        return MarketViewHolder(
            ItemMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ).apply {
            setClickListeners()
        }
    }

    override fun onBindViewHolder(viewHolder: MarketViewHolder, position: Int) {
        viewHolder.bind(currentList[position])
    }

}