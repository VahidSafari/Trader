package com.safari.traderbot.network

import com.safari.traderbot.di.Provider
import com.safari.traderbot.di.Provider.Companion.ACCESS_ID_HEADER_KEY
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.MarketOrderResponse
import com.safari.traderbot.model.balanceinfo.Data
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CoinexService {

    @GET("market/list")
    suspend fun getMarketList(): GenericResponse<List<String>?>

    @GET("balance/info")
    suspend fun getBalanceInfo(
        @Query("tonce") tonce: Long,
        @Query(ACCESS_ID_HEADER_KEY) accessId: String = Provider.ACCESS_ID_VALUE,
        @Header("authorization") authorization: String =
            com.safari.traderbot.utils.generateSignatureByParamMap(
                Pair("tonce", tonce.toString()),
                Pair(ACCESS_ID_HEADER_KEY, Provider.ACCESS_ID_VALUE)
            )
    ): GenericResponse<Data?>

    @POST("order/market")
    suspend fun submitMarketOrder(
        @Query("market") market: String,
        @Query("type") type: String,
        @Query("amount") amount: String,
        @Query("price") price: String,
        @Query("tonce") tonce: Long,
        @Header("authorization") authorization: String =
            com.safari.traderbot.utils.generateSignatureByParamMap(
                Pair("market", market),
                Pair("type", type),
                Pair("amount", amount),
                Pair("price", price),
                Pair("tonce", tonce.toString()),
                Pair(ACCESS_ID_HEADER_KEY, Provider.ACCESS_ID_VALUE)
            )
    ): GenericResponse<MarketOrderResponse>

}