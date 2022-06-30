package com.safari.traderbot.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.safari.traderbot.entity.MarketEntity

@Database(
    entities = [MarketEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MarketDB : RoomDatabase() {
    abstract fun getMarketDao(): MarketDao

    companion object {
        private var INSTANCE: RoomDatabase? = null
    }

}