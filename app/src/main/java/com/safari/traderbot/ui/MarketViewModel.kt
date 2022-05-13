package com.safari.traderbot.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safari.traderbot.data.MarketDefaultDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarketViewModel : ViewModel() {

    private val marketDataSource = MarketDefaultDataSource()

    var marketFlow = marketDataSource.marketFlow

    fun getMarkets() {
        viewModelScope.launch(Dispatchers.IO) {
            marketDataSource.getMarketList()
        }
    }

    suspend fun searchInMarkets(phrase: String) {
        viewModelScope.launch(Dispatchers.IO) {
            marketDataSource.searchInMarkets(phrase)
        }
    }

}