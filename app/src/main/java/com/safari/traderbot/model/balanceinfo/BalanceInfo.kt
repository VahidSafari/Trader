package com.safari.traderbot.model.balanceinfo

import com.google.gson.annotations.SerializedName


data class BalanceInfo(
    @SerializedName("BTC") var BTC: MarketBalanceInfo?,
    @SerializedName("USDT") var USDT: MarketBalanceInfo?,
    @SerializedName("CET") var CET: MarketBalanceInfo?,
    @SerializedName("ETH") var ETH: MarketBalanceInfo?,
    @SerializedName("DOGE") var DOGE: MarketBalanceInfo?,
)