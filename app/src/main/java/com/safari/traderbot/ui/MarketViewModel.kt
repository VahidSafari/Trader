package com.safari.traderbot.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safari.traderbot.data.MarketDefaultDataSource
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.Market
import com.safari.traderbot.model.market.MarketDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MarketViewModel : ViewModel() {

    companion object {
        const val MIN_AMOUNT_UNINITIALIZED = -1.1
    }

    private val marketDataSource = MarketDefaultDataSource()

    val markets = MutableLiveData<List<Market>>()
    val searchResult = MutableLiveData<List<Market>>()

    var minAmount = MutableLiveData(MIN_AMOUNT_UNINITIALIZED)

    fun getMarkets() {
        Log.d("flowtest", "get market in view model called!")
        viewModelScope.launch(Dispatchers.IO) {
            marketDataSource.getMarketList()
            marketDataSource.marketFlow.collectLatest { markets.postValue(it) }
        }
    }

    fun searchInMarkets(phrase: String) {
        Log.d("searchlist", marketDataSource.searchInMarkets(phrase).toString())
        searchResult.value = marketDataSource.searchInMarkets(phrase)
    }

    fun getLastFetchedAllMarkets() {
        searchResult.value = marketDataSource.markets
    }

    suspend fun getMarketDetail(marketName: String): GenericResponse<MarketDetail?> {
        return marketDataSource.getSingleMarketInfo(marketName)
    }

    fun resetMinAmount() {
        minAmount.value = MIN_AMOUNT_UNINITIALIZED
    }

}