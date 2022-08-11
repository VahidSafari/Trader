package com.safari.traderbot.di

import com.safari.traderbot.data.AccountDataSource
import com.safari.traderbot.network.CoinexService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AccountModule {

    @Singleton
    @Provides
    fun provideAccountDataSource(coinexService: CoinexService) = AccountDataSource(coinexService)

}