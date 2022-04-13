package com.safari.traderbot.model.balanceinfo

import com.google.gson.annotations.SerializedName

data class BalanceInfoParam(
    @SerializedName("access_id") val accessId: String,
    @SerializedName("tonec") val tonec: Int,
)
