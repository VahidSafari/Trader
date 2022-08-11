package com.safari.traderbot.data

import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.balanceinfo.BalanceInfo
import com.safari.traderbot.network.CoinexService
import javax.inject.Inject

class AccountDataSource @Inject constructor(
    private val coinexSource: CoinexService
){

    suspend fun getBalanceInfo(): GenericResponse<BalanceInfo?> =
        coinexSource.getBalanceInfo(System.currentTimeMillis())

}