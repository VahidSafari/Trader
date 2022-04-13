package com.safari.traderbot.model

data class GenericResponse<T>(
    val code: Int,
    val data: T,
    val message: String
)