package com.safari.traderbot.data

import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.Market
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.StockTick
import com.safari.traderbot.model.marketorder.MarketOrderParam
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse
import com.safari.traderbot.ui.AllMarketsMarketModel
import kotlinx.coroutines.flow.Flow

interface MarketDataSource {
    fun getMarketInfo(marketName: String): Flow<StockTick>
    suspend fun getSingleMarketInfo(marketName: String): GenericResponse<MarketDetail?>
    suspend fun getSingleMarketStatistics(marketName: String): GenericResponse<SingleMarketStatisticsResponse>
    suspend fun getMarketList()
    fun searchInMarkets(phrase: String): List<Market>
    suspend fun putMarketOrder(marketOrderParamView: MarketOrderParamView): GenericResponse<MarketOrderResponse>
    fun updateMarketModel(marketModel: Market)
}