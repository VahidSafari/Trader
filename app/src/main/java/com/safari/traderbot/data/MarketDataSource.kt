package com.safari.traderbot.data

import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.Market
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.StockTick
import com.safari.traderbot.model.marketorder.MarketOrderParam
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import kotlinx.coroutines.flow.Flow

interface MarketDataSource {
    fun getMarketInfo(): Flow<StockTick>
    suspend fun getSingleMarketInfo(marketName: String): GenericResponse<MarketDetail?>
    suspend fun getMarketList()
    fun searchInMarkets(phrase: String): List<Market>
    suspend fun putMarketOrder(marketOrderParamView: MarketOrderParamView): GenericResponse<MarketOrderResponse>
}