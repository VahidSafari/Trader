package com.safari.traderbot.model

import com.safari.traderbot.entity.MarketEntity

data class AllMarketsMarketModel(
    val marketName: String,
    val isFavourite: Boolean,
    val price: Double
) {
    fun toMarketEntity() = MarketEntity(marketName, isFavourite, price)
}
