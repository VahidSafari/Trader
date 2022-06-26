package com.safari.traderbot.model

import com.safari.traderbot.ui.AllMarketsMarketModel

data class Market(val id: Int, val name: String, val isFavourite: Boolean) {
    fun toAllMarketsModel() = AllMarketsMarketModel(id, name, isFavourite)
}
