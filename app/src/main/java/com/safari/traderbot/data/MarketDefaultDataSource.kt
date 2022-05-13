package com.safari.traderbot.data

import com.safari.traderbot.di.Provider
import com.safari.traderbot.model.Market
import com.safari.traderbot.model.StockTick
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf

class MarketDefaultDataSource: MarketDataSource {

    private val coinexService = Provider.getCoinexService()

    lateinit var marketFlow: Flow<Market>

    override fun getMarketInfo(): Flow<StockTick> {
        TODO("Not yet implemented")
    }

    override suspend fun getMarketList() {
        marketFlow = flowOf(*coinexService.getMarketList().data!!.toTypedArray())
    }

    override fun searchInMarkets(phrase: String) {
        marketFlow = marketFlow.filter { it.name.contains(phrase) }
    }

}