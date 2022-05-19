package com.safari.traderbot.model

import com.safari.traderbot.network.CoinexStatusCode

data class GenericResponse<T>(
    val code: CoinexStatusCode,
    val data: T,
    val message: String
) {
    fun isSuccessful() = code == CoinexStatusCode.SUCCEEDED
}