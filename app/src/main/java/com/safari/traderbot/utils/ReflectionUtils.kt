package com.safari.traderbot.utils

import kotlin.reflect.KProperty1

@Suppress("UNCHECKED_CAST")
inline fun <R, reified T> readInstanceProperty(instance: T, propertyName: String): R {
    val property = T::class.members
        // don't cast here to <Any, R>, it would succeed silently
        .first { it.name == propertyName } as KProperty1<T, *>
    // force a invalid cast exception if incorrect type here
    return property.get(instance) as R
}