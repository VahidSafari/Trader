package com.safari.traderbot.ui

import com.safari.traderbot.model.Market

data class AllMarketsMarketModel(
    val id: Int,
    val marketName: String,
    val isFavourite: Boolean
) {
    fun toMarketModel() = Market(id, marketName, isFavourite)
}
