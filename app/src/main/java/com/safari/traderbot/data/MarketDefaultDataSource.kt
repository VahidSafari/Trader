package com.safari.traderbot.data

import android.util.Log
import com.safari.traderbot.di.Provider
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.Market
import com.safari.traderbot.model.StockTick
import com.safari.traderbot.model.market.MarketDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class MarketDefaultDataSource : MarketDataSource {

    private val coinexService = Provider.getCoinexService()

    var marketFlow: Flow<List<Market>> = emptyFlow()

    var markets: List<Market> = emptyList()

    override fun getMarketInfo(): Flow<StockTick> {
        TODO("Not yet implemented")
    }

    override suspend fun getSingleMarketInfo(marketName: String): GenericResponse<MarketDetail?> {
        return coinexService.getMarketDetail(marketName)
    }

    override suspend fun getMarketList() {
        Log.d("flowtest", "market list received!")
        markets =
            coinexService.getMarketList().data!!.mapIndexed { index, str -> Market(index, str) }
        marketFlow = flowOf(markets)
    }

    override fun searchInMarkets(phrase: String): List<Market> {
        Log.d("searchlist", markets.toString())
        Log.d("searchlist", markets.filter { it.name.lowercase().contains(phrase) }.toString())
        return markets.filter { it.name.lowercase().contains(phrase) }
    }

}