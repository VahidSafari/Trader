package com.safari.traderbot.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ItemMarketBinding
import com.safari.traderbot.entity.MarketEntity

class MarketAdapter(
    val marketViewModel: MarketViewModel
): ListAdapter<MarketEntity, MarketAdapter.MarketViewHolder>(
    object : DiffUtil.ItemCallback<MarketEntity>() {
        override fun areItemsTheSame(p0: MarketEntity, p1: MarketEntity): Boolean {
            return p0.name == p1.name
        }

        override fun areContentsTheSame(p0: MarketEntity, p1: MarketEntity): Boolean {
            return p0.name == p1.name
        }

    }
) {

    lateinit var selectedMarket: SelectedMarketModel

    inner class MarketViewHolder(private val binding: ItemMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var market: MarketEntity

        fun bind(market: MarketEntity) {
            this.market = market
            binding.marketName.text = market.name
            if (::selectedMarket.isInitialized && this.market.name == selectedMarket.name) {
                setItemBackgroundColor(R.color.green)
            } else {
                setItemBackgroundColor(R.color.grey)
            }
        }

        fun setClickListeners() {
            this.itemView.setOnClickListener {
                val clickedMarketName = binding.marketName.text.toString()
                if (::selectedMarket.isInitialized) {
                    if (selectedMarket.name != clickedMarketName) {
                        marketViewModel.resetMinAmount()
                    }
                    notifyDataSetChanged()
                }
                setItemBackgroundColor(R.color.green)
                selectedMarket = SelectedMarketModel(this.layoutPosition, clickedMarketName)
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

data class SelectedMarketModel(
    val position: Int,
    val name: String
)