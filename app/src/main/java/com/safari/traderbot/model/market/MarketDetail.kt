package com.safari.traderbot.model.market

import com.google.gson.annotations.SerializedName

data class MarketDetail(
    @SerializedName("taker_fee_rate") var takerFeeRate: String? = null,
    @SerializedName("pricing_name") var pricingName: String? = null,
    @SerializedName("trading_name") var tradingName: String? = null,
    @SerializedName("min_amount") var minAmount: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("trading_decimal") var tradingDecimal: Int? = null,
    @SerializedName("maker_fee_rate") var makerFeeRate: String? = null,
    @SerializedName("pricing_decimal") var pricingDecimal: Int? = null
)
