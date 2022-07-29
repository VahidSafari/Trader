package com.safari.traderbot.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.safari.traderbot.entity.MarketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(market: MarketEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(markets: List<MarketEntity>)

    @Update
    suspend fun update(market: MarketEntity)

    @Update
    suspend fun update(market: List<MarketEntity>)

    @Query("SELECT * FROM markets")
    fun getAll(): LiveData<List<MarketEntity>>

    @Query("SELECT * FROM markets WHERE name LIKE :phrase")
    fun search(phrase : String): LiveData<List<MarketEntity>>

    @Query("SELECT * FROM markets WHERE isFavourite")
    fun getFavouriteMarketsObservable(): LiveData<List<MarketEntity>>

    @Query("SELECT * FROM markets WHERE isFavourite")
    fun getFavouriteMarkets(): List<MarketEntity>

}