package com.safari.traderbot.model.balanceinfo

import com.google.gson.annotations.SerializedName


data class MarketBalanceInfo(
    @SerializedName("available") var available: String = "0.0",
    @SerializedName("frozen") var frozen: String = "0.0"
)