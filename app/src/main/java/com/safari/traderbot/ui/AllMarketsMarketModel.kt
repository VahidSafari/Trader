package com.safari.traderbot.ui

import com.safari.traderbot.entity.MarketEntity

data class AllMarketsMarketModel(
    val id: Int,
    val marketName: String,
    val isFavourite: Boolean
) {
    fun toMarketModel() = MarketEntity(id, marketName, isFavourite)
}
