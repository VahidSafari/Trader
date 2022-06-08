package com.safari.traderbot.model.marketorder

import com.google.gson.annotations.SerializedName
import com.safari.traderbot.di.Provider
import com.safari.traderbot.di.Provider.Companion.ACCESS_ID_HEADER_KEY

data class MarketOrderParam(
    val market: String,
    val type: String,
    val amount: String,
    val tonce: String,
    @SerializedName(ACCESS_ID_HEADER_KEY)
    val accessId: String = Provider.ACCESS_ID_VALUE
)
