package com.safari.traderbot.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.safari.traderbot.ui.AllMarketsMarketModel

@Entity(
    tableName = "markets"
)
data class MarketEntity(
    @PrimaryKey
    val name: String,
    val isFavourite: Boolean
    ) {
    fun toAllMarketsModel() = AllMarketsMarketModel(name, isFavourite)
}
