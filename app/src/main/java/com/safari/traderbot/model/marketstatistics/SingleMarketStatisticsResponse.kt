package com.safari.traderbot.model.marketstatistics

import com.google.gson.annotations.SerializedName

data class SingleMarketStatisticsResponse(
    @SerializedName("date")
    val date: Long,
    @SerializedName("ticker")
    val ticker: Ticker
)