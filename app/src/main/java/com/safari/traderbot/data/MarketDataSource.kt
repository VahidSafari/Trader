package com.safari.traderbot.data

import com.safari.traderbot.model.StockTick
import kotlinx.coroutines.flow.Flow

interface MarketDataSource {
    fun getMarketInfo(): Flow<StockTick>
}