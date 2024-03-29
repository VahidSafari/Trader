package com.safari.traderbot.model.marketorder

import com.google.gson.annotations.SerializedName

data class MarketOrderResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("create_time") val createTime: Long,
    @SerializedName("finished_time") val finishedTime: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("price") val price: Double,
    @SerializedName("deal_amount") val dealAmount: Double,
    @SerializedName("deal_money") val dealMoney: Double,
    @SerializedName("deal_fee") val dealFee: Double,
    @SerializedName("stock_fee") val stockFee: Double,
    @SerializedName("money_fee") val moneyFee: Double,
    @SerializedName(" asset_fee") val assetFee: Double,
    @SerializedName("fee_asset") val feeAsset: String,
    @SerializedName("fee_discount") val feeDiscount: Double,
    @SerializedName("avg_price") val avgPrice: Double,
    @SerializedName("market") val market: String,
    @SerializedName("left") val left: String,
    @SerializedName("maker_fee_rate") val makerFeeRate: Double,
    @SerializedName("taker_fee_rate") val takerFeeRate: Double,
    @SerializedName("order_type") val orderType: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("client_id ") val clientId: String,
    @SerializedName("source_id") val sourceId: Long
)