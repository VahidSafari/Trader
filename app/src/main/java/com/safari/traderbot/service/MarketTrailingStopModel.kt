package com.safari.traderbot.service

data class MarketTrailingStopModel(
    val stopPercent: Double,
    val lastSeenPrice: Double,
    val maxSeenPrice: Double
)