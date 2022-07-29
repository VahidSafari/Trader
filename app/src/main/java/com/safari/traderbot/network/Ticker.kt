package com.safari.traderbot.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.safari.traderbot.model.marketstatistics.TickerDetails

data class Ticker(
    @SerializedName("ticker")
    @Expose
    val tickerDetails: Map<String, TickerDetails>
)