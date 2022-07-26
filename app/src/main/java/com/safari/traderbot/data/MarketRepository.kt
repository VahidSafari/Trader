package com.safari.traderbot.data

import androidx.lifecycle.LiveData
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.entity.MarketEntity
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.StockTick
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface MarketRepository {
    fun getMarketInfo(marketName: String): Flow<StockTick>
    suspend fun getSingleMarketInfo(marketName: String): GenericResponse<MarketDetail?>
    suspend fun getSingleMarketStatistics(marketName: String): GenericResponse<SingleMarketStatisticsResponse>
    suspend fun getMarketList(): LiveData<List<MarketEntity>>
    suspend fun putMarketOrder(marketOrderParamView: MarketOrderParamView): GenericResponse<MarketOrderResponse>
    suspend fun updateMarketModel(marketEntity: MarketEntity)
    suspend fun fetchPriceUpdateOfFavouriteMarkets()
}