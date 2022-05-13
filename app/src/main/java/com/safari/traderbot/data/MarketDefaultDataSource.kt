package com.safari.traderbot.data

import android.util.Log
import com.safari.traderbot.di.Provider
import com.safari.traderbot.model.Market
import com.safari.traderbot.model.StockTick
import kotlinx.coroutines.flow.*
import java.util.*

class MarketDefaultDataSource: MarketDataSource {

    private val coinexService = Provider.getCoinexService()

    var marketFlow: Flow<List<Market>> = emptyFlow()

    var markets: List<Market> = emptyList()

    override fun getMarketInfo(): Flow<StockTick> {
        TODO("Not yet implemented")
    }

    override suspend fun getMarketList() {
        Log.d("flowtest", "market list received!")
        markets = coinexService.getMarketList().data!!.mapIndexed { index, str -> Market(index, str) }
        marketFlow = flowOf(markets)
    }

    override fun searchInMarkets(phrase: String): List<Market> {
        Log.d("searchlist", markets.toString())
        Log.d("searchlist", markets.filter { it.name.lowercase().contains(phrase) }.toString())
        return markets.filter { it.name.lowercase().contains(phrase) }
    }

}