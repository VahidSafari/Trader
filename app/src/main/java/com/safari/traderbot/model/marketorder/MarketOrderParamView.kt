package com.safari.traderbot.model.marketorder

import com.safari.traderbot.model.ORDER_TYPE_BUY
import com.safari.traderbot.model.ORDER_TYPE_SELL
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse

data class MarketOrderParamView(
    val marketName: String,
    val orderType: String,
    val selectedMarketOrderAmount: Double,
    val tonce: Long,
) {

    fun toMarketOrderParam(): MarketOrderParam {

        return MarketOrderParam(
            market = this.marketName,
            type = this.orderType,
            amount = selectedMarketOrderAmount.toString(),
            tonce = this.tonce.toString()
        )
    }

}
