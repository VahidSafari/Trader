package com.safari.traderbot.model.balanceinfo

import com.google.gson.annotations.SerializedName


data class CET(

    @SerializedName("available") var available: String? = null,
    @SerializedName("frozen") var frozen: String? = null

)