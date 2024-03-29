package com.safari.traderbot.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.safari.traderbot.db.MarketDao
import com.safari.traderbot.entity.MarketEntity
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.ORDER_TYPE_BUY
import com.safari.traderbot.model.StockTick
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse
import com.safari.traderbot.network.CoinexService
import com.safari.traderbot.network.CoinexStatusCode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val coinexService: CoinexService,
    private val marketDao: MarketDao
) : MarketRepository {

    override fun getMarketInfo(marketName: String): Flow<StockTick> {
        TODO("Not yet implemented")
    }

    override suspend fun getSingleMarketInfo(marketName: String): GenericResponse<MarketDetail?> {
        return coinexService.getMarketDetail(marketName)
    }

    override suspend fun getSingleMarketStatistics(marketName: String): GenericResponse<SingleMarketStatisticsResponse> {
        return coinexService.getSingleMarketStatistics(marketName)
    }

    override suspend fun getMarketList(): LiveData<List<MarketEntity>> {
        Log.d("flowtest", "market list received!")
        val marketList = coinexService.getMarketList().data!!
        marketDao.insert(
            marketList.map { marketName ->
                MarketEntity(marketName, false, 0.0)
            }
        )
        return marketDao.getAll()
    }



    override suspend fun putMarketOrder(marketOrderParamView: MarketOrderParamView): GenericResponse<MarketOrderResponse> {
        val marketOrderParam = marketOrderParamView.toMarketOrderParam()
        return coinexService.submitMarketOrder(marketOrderParam)
    }

    override suspend fun updateMarketModel(marketEntity: MarketEntity) {
        marketDao.update(marketEntity)
    }

    override suspend fun fetchPriceUpdateOfFavouriteMarkets(): List<MarketEntity> {
        val favouriteMarkets = marketDao.getFavouriteMarkets()
        val allMarketsTickerResponse = coinexService.getAllMarketsTicker()
        val toBeUpdatedMarketEntities = mutableListOf<MarketEntity>()
        if (allMarketsTickerResponse.code == CoinexStatusCode.SUCCEEDED) {
            favouriteMarkets.forEach { favouritedMarket ->
                allMarketsTickerResponse.data.tickerDetails[favouritedMarket.name]?.buy?.toDoubleOrNull()?.let { newPrice ->
                    toBeUpdatedMarketEntities.add(favouritedMarket.copy(price = newPrice))
                }
            }
        }
        marketDao.update(toBeUpdatedMarketEntities)
        return marketDao.getFavouriteMarkets()
    }

}