package com.safari.traderbot.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.safari.traderbot.MarketApplication
import com.safari.traderbot.entity.MarketEntity

@Database(
    entities = [MarketEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MarketDB : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "MARKET_DB"
    }

    abstract fun getMarketDao(): MarketDao

}