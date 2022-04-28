package com.safari.traderbot.model

data class StockTick(
    val openPrice: Double,
    val closePrice: Double,
    val minPrice: Double,
    val maxPrice: Double,
)