package com.safari.traderbot.ui

import com.safari.traderbot.entity.MarketEntity

data class AllMarketsMarketModel(
    val marketName: String,
    val isFavourite: Boolean
) {
    fun toMarketModel() = MarketEntity(marketName, isFavourite)
}
