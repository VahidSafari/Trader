package com.safari.traderbot.data

import androidx.lifecycle.LiveData
import com.safari.traderbot.entity.MarketEntity
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.StockTick
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class MarketMockRepositoryImpl : MarketRepository {

    override fun getMarketInfo(marketName: String) = flow {
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

    override suspend fun getSingleMarketInfo(marketName: String): GenericResponse<MarketDetail?> {
        TODO("Not yet implemented")
    }

    override suspend fun getSingleMarketStatistics(marketName: String): GenericResponse<SingleMarketStatisticsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getMarketList(): LiveData<List<MarketEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun putMarketOrder(marketOrderParamView: MarketOrderParamView): GenericResponse<MarketOrderResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMarketModel(marketEntity: MarketEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchPriceUpdateOfFavouriteMarkets() {
        TODO("Not yet implemented")
    }

}