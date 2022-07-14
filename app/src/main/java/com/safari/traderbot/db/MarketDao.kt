package com.safari.traderbot.db

import androidx.room.*
import com.safari.traderbot.entity.MarketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(market: MarketEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(markets: List<MarketEntity>)

    @Update
    fun update(market: MarketEntity)

    @Query("SELECT * from markets")
    fun getAll(): Flow<List<MarketEntity>>

}