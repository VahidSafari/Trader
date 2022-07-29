package com.safari.traderbot.di

import android.app.Application
import androidx.room.Room
import com.safari.traderbot.db.MarketDB
import com.safari.traderbot.db.MarketDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(app: Application) =
        Room.databaseBuilder(
            app,
            MarketDB::class.java,
            MarketDB.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideMarketDao(db: MarketDB): MarketDao = db.getMarketDao()

}