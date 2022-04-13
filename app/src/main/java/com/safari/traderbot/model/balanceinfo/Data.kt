package com.safari.traderbot.model.balanceinfo

import com.google.gson.annotations.SerializedName


data class Data(

    @SerializedName("BTC") var BTC: BTC? = BTC(),
    @SerializedName("USDT") var USDT: USDT? = USDT(),
    @SerializedName("CET") var CET: CET? = CET(),
    @SerializedName("ETH") var ETH: ETH? = ETH()

)