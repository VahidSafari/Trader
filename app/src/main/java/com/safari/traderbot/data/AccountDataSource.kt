package com.safari.traderbot.data

import com.safari.traderbot.di.Provider
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.balanceinfo.BalanceInfo

class AccountDataSource {

    val coinexSource = Provider.getCoinexService()

    suspend fun getBalanceInfo(): GenericResponse<BalanceInfo?> = coinexSource.getBalanceInfo(System.currentTimeMillis())

}