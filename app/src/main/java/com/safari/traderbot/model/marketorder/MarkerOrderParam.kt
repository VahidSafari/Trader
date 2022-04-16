package com.safari.traderbot.model.marketorder

import com.safari.traderbot.di.Provider

data class MarkerOrderParam(
    val market: String,
    val type: String,
    val amount: String,
    val tonce: String,
    val accessId: String = Provider.ACCESS_ID_VALUE
)
