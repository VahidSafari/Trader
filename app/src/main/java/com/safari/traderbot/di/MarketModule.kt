package com.safari.traderbot.di

import com.safari.traderbot.data.MarketRepository
import com.safari.traderbot.data.MarketRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MarketModule {

    @Binds
    fun getMarketDataSource(marketDataSource: MarketRepositoryImpl): MarketRepository

}