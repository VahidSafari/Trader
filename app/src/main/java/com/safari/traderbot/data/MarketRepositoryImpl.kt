package com.safari.traderbot.data

import android.util.Log
import com.safari.traderbot.db.MarketDao
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.entity.MarketEntity
import com.safari.traderbot.model.ORDER_TYPE_BUY
import com.safari.traderbot.model.StockTick
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse
import com.safari.traderbot.network.CoinexService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val coinexService: CoinexService,
    private val marketDao: MarketDao
): MarketRepository {

    override var marketFlow: MutableStateFlow<List<MarketEntity>> = MutableStateFlow(emptyList())

    override var markets: ArrayList<MarketEntity> = arrayListOf()

    override fun getMarketInfo(marketName: String): Flow<StockTick> {
        TODO("Not yet implemented")
    }

    override suspend fun getSingleMarketInfo(marketName: String): GenericResponse<MarketDetail?> {
        return coinexService.getMarketDetail(marketName)
    }

    override suspend fun getSingleMarketStatistics(marketName: String): GenericResponse<SingleMarketStatisticsResponse> {
        return coinexService.getSingleMarketStatistics(marketName)
    }

    override suspend fun getMarketList() {
        Log.d("flowtest", "market list received!")
        val marketList = coinexService.getMarketList().data!!
        markets = ArrayList(marketList.mapIndexed { index, str ->
            MarketEntity(
                str,
                findMarketByMarketName(str)?.isFavourite?:false
            )
        })
        marketFlow.value = markets
    }

    override fun searchInMarkets(phrase: String): List<MarketEntity> {
        Log.d("searchlist", markets.toString())
        Log.d("searchlist", markets.filter { it.name.lowercase().contains(phrase) }.toString())
        return markets.filter { it.name.lowercase().contains(phrase) }
    }

    override suspend fun putMarketOrder(marketOrderParamView: MarketOrderParamView): GenericResponse<MarketOrderResponse> {
        val marketOrderParam = if (marketOrderParamView.orderType == ORDER_TYPE_BUY) {
            val singleMarketStatistics = getSingleMarketStatistics(marketOrderParamView.marketName)
            marketOrderParamView.toMarketOrderParamForBuyOrder(singleMarketStatistics.data)
        } else {
            marketOrderParamView.toMarketOrderParamForSellOrder()
        }
        return coinexService.submitMarketOrder(marketOrderParam)
    }

    override fun updateMarketModel(market: MarketEntity) {
        markets[markets.indexOfFirst { it.name == market.name }] = market
        marketFlow.value = markets
    }

    private fun findMarketByMarketName(marketName: String): MarketEntity? {
        return markets.firstOrNull { it.name == marketName }
    }

}