package com.safari.traderbot.model.market

import com.google.gson.annotations.SerializedName

data class MarketDetailParam(
    @SerializedName("market")
    val marketName: String
)