package com.safari.traderbot.data

import com.safari.traderbot.model.StockTick
import kotlinx.coroutines.flow.flow

class MarketMockDataSourceImpl : MarketDataSource {

    override fun getMarketInfo() = flow {
        emit(StockTick(2.2, 3.3, 1.6, 4.5))
        kotlinx.coroutines.delay(1000)

        emit(StockTick(3.3, 3.5, 1.6, 4.5))
        kotlinx.coroutines.delay(1000)

        emit(StockTick(3.5, 4.1, 1.6, 4.5))
        kotlinx.coroutines.delay(1000)

        emit(StockTick(4.1, 3.25, 1.6, 4.5))
        kotlinx.coroutines.delay(1000)

        emit(StockTick(3.4, 6.8, 1.6, 4.5))
    }

    override fun getMarketList() {
        TODO("Not yet implemented")
    }

}