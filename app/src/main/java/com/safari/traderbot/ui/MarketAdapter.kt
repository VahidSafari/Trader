package com.safari.traderbot.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.databinding.ItemMarketBinding

class MarketAdapter(
    val defaultItemColor: Int,
    val selectedItemColor: Int,
) : ListAdapter<String, MarketAdapter.MarketViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(p0: String, p1: String): Boolean {
            return p0 == p1;
        }

        override fun areContentsTheSame(p0: String, p1: String): Boolean {
            return p0 == p1;
        }

    }
) {

    lateinit var selectedMarket: Pair<Int, String>
    lateinit var attachedRecyclerView: RecyclerView

    inner class MarketViewHolder(private val binding: ItemMarketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(marketName: String) {
            binding.marketName.text = marketName
        }

        fun setClickListeners() {
            binding.root.setOnClickListener {
                if (::selectedMarket.isInitialized) {
                    (attachedRecyclerView.findViewHolderForLayoutPosition(selectedMarket.first) as MarketViewHolder)
                        .setItemBackgroundColor(defaultItemColor)
                }
                setItemBackgroundColor(selectedItemColor)
                selectedMarket = Pair(this.layoutPosition, binding.marketName.text.toString())
            }
        }

        private fun setItemBackgroundColor(color: Int) {
            binding.root.setBackgroundColor(color)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MarketViewHolder {
        return MarketViewHolder(
            ItemMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ).apply { setClickListeners() }
    }

    override fun onBindViewHolder(viewHolder: MarketViewHolder, position: Int) {
        viewHolder.bind(currentList[position])
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        attachedRecyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }
}