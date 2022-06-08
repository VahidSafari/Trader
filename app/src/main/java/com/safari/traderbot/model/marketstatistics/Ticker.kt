package com.safari.traderbot.model.marketstatistics

import com.google.gson.annotations.SerializedName

data class Ticker(
    @SerializedName("buy") var buy: String? = null,
    @SerializedName("buy_amount") var buyAmount: String? = null,
    @SerializedName("open") var open: String? = null,
    @SerializedName("high") var high: String? = null,
    @SerializedName("last") var last: String? = null,
    @SerializedName("low") var low: String? = null,
    @SerializedName("sell") var sell: String? = null,
    @SerializedName("sell_amount") var sellAmount: String? = null,
    @SerializedName("vol") var vol: String? = null
)