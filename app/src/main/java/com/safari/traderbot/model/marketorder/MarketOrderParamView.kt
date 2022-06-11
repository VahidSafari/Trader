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

    fun toMarketOrderParamForBuyOrder(singleMarketStatistics: SingleMarketStatisticsResponse): MarketOrderParam {
        val selectedMarketOrderTickerValue = when (this.orderType.lowercase()) {
            ORDER_TYPE_BUY -> {
                singleMarketStatistics.ticker.buy!!.toDouble()
            }
            ORDER_TYPE_SELL -> {
                singleMarketStatistics.ticker.sell!!.toDouble()
            }
            else -> {
                0.0
            }
        }
        val selectedMarketOrderAmount =
            this.selectedMarketOrderAmount * selectedMarketOrderTickerValue
        return MarketOrderParam(
            market = this.marketName,
            type = this.orderType,
            amount = selectedMarketOrderAmount.toString(),
            tonce = this.tonce.toString()
        )
    }

    fun toMarketOrderParamForSellOrder(): MarketOrderParam =
        MarketOrderParam(
            market = this.marketName,
            type = this.orderType,
            amount = this.selectedMarketOrderAmount.toString(),
            tonce = this.tonce.toString()
        )

}
