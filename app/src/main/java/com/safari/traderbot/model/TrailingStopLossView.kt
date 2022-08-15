package com.safari.traderbot.model

data class TrailingStopLossView(
    val marketName: String,
    val currentPrice: Double,
    val lastPriceToMaxSeenPriceRatio: Double,
)
