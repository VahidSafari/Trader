package com.safari.traderbot.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.safari.traderbot.databinding.ItemTslBinding
import com.safari.traderbot.model.TrailingStopLossView

class TSLAdapter : ListAdapter<TrailingStopLossView, TSLAdapter.TSLViewHolder>(object :
    DiffUtil.ItemCallback<TrailingStopLossView>() {
    override fun areItemsTheSame(
        oldItem: TrailingStopLossView,
        newItem: TrailingStopLossView
    ): Boolean {
        return oldItem.marketName == newItem.marketName
    }

    override fun areContentsTheSame(
        oldItem: TrailingStopLossView,
        newItem: TrailingStopLossView
    ): Boolean {
        return oldItem.currentPrice == newItem.currentPrice &&
                oldItem.lastPriceToMaxSeenPriceRatio == newItem.lastPriceToMaxSeenPriceRatio
    }
}) {

    class TSLViewHolder(private val binding: ItemTslBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var trailingStopLossView: TrailingStopLossView

        fun bind(trailingStopLossView: TrailingStopLossView) {
            binding.tslView = trailingStopLossView
            this.trailingStopLossView = trailingStopLossView
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TSLViewHolder {
        return TSLViewHolder(ItemTslBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TSLViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}