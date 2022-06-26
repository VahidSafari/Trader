package com.safari.traderbot.ui

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safari.traderbot.di.Provider
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.Market
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MarketViewModel : ViewModel() {

    companion object {
        const val MIN_AMOUNT_UNINITIALIZED = -1.1
    }

    private val marketDataSource = Provider.getMarketDefaultDataSource()

    val markets = MutableLiveData<List<Market>>()
    val searchResult = MutableLiveData<List<Market>>()
    val marketOrderResult = MutableLiveData<GenericResponse<MarketOrderResponse>>()
    val favouriteLiveData = MediatorLiveData<List<Market>>()

    var minAmount = MutableLiveData(MIN_AMOUNT_UNINITIALIZED)

    init {
        favouriteLiveData.addSource(markets) {
            val onlyFavouriteMarkets = it.filter { market -> market.isFavourite }
            favouriteLiveData.value = onlyFavouriteMarkets
        }
    }

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

    fun submitMarketOrder(marketOrderParamView: MarketOrderParamView) {
        viewModelScope.launch(Dispatchers.IO) {
            marketOrderResult.postValue(marketDataSource.putMarketOrder(marketOrderParamView))
        }
    }

    fun toggleFavouriteStatus(marketModel: AllMarketsMarketModel) {
        marketDataSource.updateMarketModel(marketModel.toMarketModel())
    }

}