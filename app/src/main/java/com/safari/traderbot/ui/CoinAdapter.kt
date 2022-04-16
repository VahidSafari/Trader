package com.safari.traderbot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.databinding.ItemCoinBinding

class CoinAdapter : ListAdapter<String, CoinAdapter.CoinViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(p0: String, p1: String): Boolean {
            return p0 == p1;
        }

        override fun areContentsTheSame(p0: String, p1: String): Boolean {
            return p0 == p1;
        }

    }
) {
    class CoinViewHolder(private val binding: ItemCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coinName: String) {
            binding.coinName.text = coinName
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