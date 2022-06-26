package com.safari.traderbot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.databinding.ItemAllMarketMarketBinding

class FavouriteAdapter(private val marketListViewModel: MarketViewModel) :
    ListAdapter<AllMarketsMarketModel, FavouriteAdapter.FavouriteViewHolder>(
        object : DiffUtil.ItemCallback<AllMarketsMarketModel>() {
            override fun areItemsTheSame(
                p0: AllMarketsMarketModel,
                p1: AllMarketsMarketModel
            ): Boolean {
                return p0.id == p1.id
            }

            override fun areContentsTheSame(
                p0: AllMarketsMarketModel,
                p1: AllMarketsMarketModel
            ): Boolean {
                return p0.marketName == p1.marketName && p0.isFavourite == p1.isFavourite
            }

        }
    ) {

    inner class FavouriteViewHolder(private val binding: ItemAllMarketMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var marketModel: AllMarketsMarketModel

        fun bind(marketModel: AllMarketsMarketModel) {
            binding.itemMarket = marketModel
            this.marketModel = marketModel
        }

        fun setOnClickListeners() {
            binding.ivAllMarketFavourite.setOnClickListener {
                marketListViewModel.toggleFavouriteStatus(marketModel)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        return FavouriteViewHolder(
            ItemAllMarketMarketBinding.inflate(LayoutInflater.from(parent.context))
        ).apply { setOnClickListeners() }
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}