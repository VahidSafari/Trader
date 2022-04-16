package com.safari.traderbot.network

import com.safari.traderbot.di.Provider
import com.safari.traderbot.di.Provider.Companion.ACCESS_ID_HEADER_KEY
import com.safari.traderbot.di.Provider.Companion.ACCESS_ID_VALUE
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.model.balanceinfo.Data
import com.safari.traderbot.model.marketorder.MarkerOrderParam
import retrofit2.http.*

interface CoinexService {

    @GET("market/list")
    suspend fun getMarketList(): GenericResponse<List<String>?>

    @GET("balance/info")
    suspend fun getBalanceInfo(
        @Query("tonce") tonce: Long,
        @Query(ACCESS_ID_HEADER_KEY) accessId: String = ACCESS_ID_VALUE,
        @Header("authorization") authorization: String =
            com.safari.traderbot.utils.generateSignatureByParamMap(
                Pair("tonce", tonce.toString()),
                Pair(ACCESS_ID_HEADER_KEY, ACCESS_ID_VALUE)
            )
    ): GenericResponse<Data?>

    @POST("order/market")
    suspend fun submitMarketOrder(
        @Body body: MarkerOrderParam,
        @Header("authorization") authorization: String =
            com.safari.traderbot.utils.generateSignatureByParamMap(
                Pair("market", body.market),
                Pair("type", body.type),
                Pair("amount", body.amount),
                Pair("tonce", body.tonce),
                Pair(ACCESS_ID_HEADER_KEY, ACCESS_ID_VALUE)
            )
    ): GenericResponse<MarketOrderResponse>

}