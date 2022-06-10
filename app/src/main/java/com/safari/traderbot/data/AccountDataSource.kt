package com.safari.traderbot.data

import com.safari.traderbot.di.Provider

class AccountDataSource {

    val coinexSource = Provider.getCoinexService()

    suspend fun getBalanceInfo() = coinexSource.getBalanceInfo(System.currentTimeMillis())

}