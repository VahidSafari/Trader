package com.safari.traderbot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.databinding.ItemFavouriteMarketBinding
import com.safari.traderbot.model.FavouriteMarketModel

class FavouriteAdapter(private val marketListViewModel: MarketViewModel) :
    ListAdapter<FavouriteMarketModel, FavouriteAdapter.FavouriteViewHolder>(
        object : DiffUtil.ItemCallback<FavouriteMarketModel>() {
            override fun areItemsTheSame(
                p0: FavouriteMarketModel,
                p1: FavouriteMarketModel
            ): Boolean {
                return p0.marketName == p1.marketName
            }

            override fun areContentsTheSame(
                p0: FavouriteMarketModel,
                p1: FavouriteMarketModel
            ): Boolean {
                return p0.marketName == p1.marketName && p0.isFavourite == p1.isFavourite && p0.price == p1.price
            }

        }
    ) {

    inner class FavouriteViewHolder(private val binding: ItemFavouriteMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var marketModel: FavouriteMarketModel

        fun bind(marketModel: FavouriteMarketModel) {
            binding.itemMarket = marketModel
            this.marketModel = marketModel
        }

        fun setOnClickListeners() {
            binding.ivFavouriteToggler.setOnClickListener {
                marketListViewModel.toggleFavouriteStatus(marketModel)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        return FavouriteViewHolder(
            ItemFavouriteMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ).apply { setOnClickListeners() }
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}