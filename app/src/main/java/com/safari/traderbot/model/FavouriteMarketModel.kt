package com.safari.traderbot.model

import com.safari.traderbot.entity.MarketEntity

data class FavouriteMarketModel(
    val marketName: String,
    val isFavourite: Boolean,
    val price: Double,
    val isGoingUp: Boolean
) {
    fun toMarketEntity() = MarketEntity(marketName, isFavourite, price)
}