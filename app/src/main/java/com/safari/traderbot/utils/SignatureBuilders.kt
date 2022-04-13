package com.safari.traderbot.utils

import com.safari.traderbot.di.Provider
import com.safari.traderbot.di.Provider.Companion.ACCESS_ID_VALUE
import com.safari.traderbot.di.Provider.Companion.SECRET_KEY_VALUE

public fun generateSignatureByParamMap(
    vararg parameter: Pair<String, String>,
): String {
    val parameterMap = HashMap<String, String>()
    parameter.forEach { parameterMap[it.first] = it.second }
    parameterMap[Provider.ACCESS_ID_HEADER_KEY] = ACCESS_ID_VALUE
    return MD5Util.buildSignature(parameterMap, SECRET_KEY_VALUE)
}