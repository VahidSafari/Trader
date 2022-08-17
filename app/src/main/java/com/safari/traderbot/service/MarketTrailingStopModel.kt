package com.safari.traderbot.service

import com.safari.traderbot.model.TrailingStopLossView

data class MarketTrailingStopModel(
    val marketName: String,
    val stopPercent: Double,
    val lastSeenPrice: Double,
    val tslPrice: Double,
    val maxSeenPrice: Double,
) {

    fun toTrailingStopLossView(): TrailingStopLossView =
        TrailingStopLossView(
            marketName = marketName,
            currentPrice = lastSeenPrice,
            tslPrice = tslPrice
        )

}