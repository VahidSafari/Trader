package com.safari.traderbot.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.safari.traderbot.model.AllMarketsMarketModel

@Entity(
    tableName = "markets"
)
data class MarketEntity(
    @PrimaryKey
    val name: String,
    val isFavourite: Boolean,
    val price: Double,
) {
    fun toAllMarketsModel() = AllMarketsMarketModel(name, isFavourite, price)
}
