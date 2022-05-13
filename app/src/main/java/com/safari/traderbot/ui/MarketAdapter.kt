package com.safari.traderbot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.databinding.ItemMarketBinding
import com.safari.traderbot.model.Market

class MarketAdapter(
    val defaultItemColor: Int,
    val selectedItemColor: Int,
) : ListAdapter<Market, MarketAdapter.MarketViewHolder>(
    object : DiffUtil.ItemCallback<Market>() {
        override fun areItemsTheSame(p0: Market, p1: Market): Boolean {
            return p0 == p1
        }

        override fun areContentsTheSame(p0: Market, p1: Market): Boolean {
            return p0.name == p1.name
        }

    }
) {

    lateinit var selectedMarket: Pair<Int, String>
    lateinit var attachedRecyclerView: RecyclerView

    inner class MarketViewHolder(private val binding: ItemMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(market: Market) {
            binding.marketName.text = market.name
        }

        fun setClickListeners() {
            binding.root.setOnClickListener {
                if (::selectedMarket.isInitialized) {
                    (attachedRecyclerView.findViewHolderForAdapterPosition(selectedMarket.first) as MarketViewHolder)
                        .setItemBackgroundColor(defaultItemColor)
                }
                setItemBackgroundColor(selectedItemColor)
                selectedMarket = Pair(this.adapterPosition, binding.marketName.text.toString())
            }
        }

        private fun setItemBackgroundColor(color: Int) {
            binding.root.setBackgroundColor(color)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MarketViewHolder {
        return MarketViewHolder(
            ItemMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: MarketViewHolder, position: Int) {
        viewHolder.bind(currentList[position])
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        attachedRecyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }
}