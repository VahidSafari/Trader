package com.safari.traderbot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.databinding.ItemCoinBinding
import com.safari.traderbot.model.Market

class CoinAdapter : ListAdapter<Market, CoinAdapter.CoinViewHolder>(
    object : DiffUtil.ItemCallback<Market>() {
        override fun areItemsTheSame(m1: Market, m2: Market): Boolean {
            return m1 == m2
        }

        override fun areContentsTheSame(m1: Market, m2: Market): Boolean {
            return m1.name == m2.name
        }

    }
) {
    class CoinViewHolder(private val binding: ItemCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(market: Market) {
            binding.coinName.text = market.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CoinViewHolder {
        return CoinViewHolder(
            ItemCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: CoinViewHolder, position: Int) {
        viewHolder.bind(currentList[position])
    }
}