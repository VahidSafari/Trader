package com.safari.traderbot.db

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM markets")
    fun getAll(): LiveData<List<MarketEntity>>

    @Query("SELECT * FROM markets WHERE name LIKE :phrase")
    fun search(phrase : String): LiveData<List<MarketEntity>>

}