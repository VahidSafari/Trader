package com.safari.traderbot.network

import com.safari.traderbot.di.Provider.Companion.ACCESS_ID_HEADER_KEY
import com.safari.traderbot.di.Provider.Companion.ACCESS_ID_VALUE
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.model.balanceinfo.BalanceInfo
import com.safari.traderbot.model.marketorder.MarketOrderParam
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse
import retrofit2.http.*

interface CoinexService {

    @GET("market/list")
    suspend fun getMarketList(): GenericResponse<List<String>?>


    @GET("market/detail")
    suspend fun getMarketDetail(
        @Query("market") marketName: String
    ): GenericResponse<MarketDetail?>


    @GET("balance/info")
    suspend fun getBalanceInfo(
        @Query("tonce") tonce: Long,
        @Query(ACCESS_ID_HEADER_KEY) accessId: String = ACCESS_ID_VALUE,
        @Header("authorization") authorization: String =
            com.safari.traderbot.utils.generateSignatureByParamMap(
                Pair("tonce", tonce.toString()),
                Pair(ACCESS_ID_HEADER_KEY, ACCESS_ID_VALUE)
            )
    ): GenericResponse<BalanceInfo?>

    @POST("order/market")
    suspend fun submitMarketOrder(
        @Body body: MarketOrderParam,
        @Header("authorization") authorization: String =
            com.safari.traderbot.utils.generateSignatureByParamMap(
                Pair("market", body.market),
                Pair("type", body.type),
                Pair("amount", body.amount),
                Pair("tonce", body.tonce),
                Pair(ACCESS_ID_HEADER_KEY, ACCESS_ID_VALUE)
            )
    ): GenericResponse<MarketOrderResponse>

    @GET("market/ticker")
    suspend fun getSingleMarketStatistics(
        @Query("market") marketName: String
    ): GenericResponse<SingleMarketStatisticsResponse>


    @GET("market/ticker/all")
    suspend fun getAllMarketsTicker(): GenericResponse<Ticker>

}