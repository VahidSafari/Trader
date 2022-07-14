package com.safari.traderbot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.databinding.ItemAllMarketMarketBinding

class AllMarketsAdapter(val viewModel: MarketViewModel) :
    androidx.recyclerview.widget.ListAdapter<AllMarketsMarketModel, AllMarketsAdapter.AllMarketsViewHolder>(
        object : DiffUtil.ItemCallback<AllMarketsMarketModel>() {
            override fun areItemsTheSame(
                p0: AllMarketsMarketModel,
                p1: AllMarketsMarketModel
            ): Boolean {
                return p0.marketName == p1.marketName
            }

            override fun areContentsTheSame(
                p0: AllMarketsMarketModel,
                p1: AllMarketsMarketModel
            ): Boolean {
                return p0.marketName == p1.marketName && p0.isFavourite == p1.isFavourite
            }

        }
    ) {

    inner class AllMarketsViewHolder(private val binding: ItemAllMarketMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var marketModel: AllMarketsMarketModel

        fun bind(marketModel: AllMarketsMarketModel) {
            binding.itemMarket = marketModel
            this.marketModel = marketModel
        }

        fun setOnClickListeners() {
            binding.ivAllMarketFavourite.setOnClickListener {
                viewModel.toggleFavouriteStatus(marketModel)
                viewModel.getMarkets()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllMarketsViewHolder {
        return AllMarketsViewHolder(ItemAllMarketMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            setOnClickListeners()
        }
    }

    override fun onBindViewHolder(holder: AllMarketsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}