package com.safari.traderbot.utils

import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.balanceinfo.BalanceInfo
import com.safari.traderbot.model.balanceinfo.MarketBalanceInfo
import com.safari.traderbot.network.CoinexStatusCode
import kotlin.reflect.KProperty1

@Suppress("UNCHECKED_CAST")
inline fun <R, reified T> readInstanceProperty(instance: T, propertyName: String): R? {

/*    T::class.members.map {
        println(it.name)
    }*/

    val property = T::class.members
        // don't cast here to <Any, R>, it would succeed silently
        .first { it.name == propertyName } as KProperty1<T, *>
    // force a invalid cast exception if incorrect type here

    return property.get(instance) as R?
}


fun main() {
    val balanceInfo = BalanceInfo(
        null,
        null,
        null,
        null,
        MarketBalanceInfo("50.1", "10.1")
    )
    val currentMarketName = "DOGE"
    val property = readInstanceProperty<MarketBalanceInfo, BalanceInfo>(balanceInfo, currentMarketName)
    print(property?.available?.toDouble())
}