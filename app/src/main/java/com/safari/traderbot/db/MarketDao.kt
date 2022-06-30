package com.safari.traderbot.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.safari.traderbot.entity.MarketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {

    @Insert
    fun insert(market: MarketEntity)

    @Insert
    fun insert(markets: List<MarketEntity>)

    @Update
    fun update(market: MarketEntity)

    @Query("SELECT * from markets")
    fun getAll(): Flow<List<MarketEntity>>

}