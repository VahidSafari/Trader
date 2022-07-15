package com.safari.traderbot.ui

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safari.traderbot.data.MarketRepository
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.entity.MarketEntity
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    companion object {
        const val MIN_AMOUNT_UNINITIALIZED = -1.1
    }

    val markets = MutableLiveData<List<MarketEntity>>()
    val searchResult = MutableLiveData<List<MarketEntity>>()
    val marketOrderResult = MutableLiveData<GenericResponse<MarketOrderResponse>>()
    val favouriteLiveData = MediatorLiveData<List<MarketEntity>>()

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
            marketRepository.getMarketList().observeForever {
                markets.postValue(it ?: listOf())
            }
        }
    }

    fun searchInMarkets(phrase: String) {
        searchResult.value = markets.value?.filter { it.name.lowercase().contains(phrase) }
    }

    fun getLastFetchedAllMarkets() {
        searchResult.value = markets.value
    }

    suspend fun getMarketDetail(marketName: String): GenericResponse<MarketDetail?> {
        return marketRepository.getSingleMarketInfo(marketName)
    }

    fun resetMinAmount() {
        minAmount.value = MIN_AMOUNT_UNINITIALIZED
    }

    fun submitMarketOrder(marketOrderParamView: MarketOrderParamView) {
        viewModelScope.launch(Dispatchers.IO) {
            marketOrderResult.postValue(marketRepository.putMarketOrder(marketOrderParamView))
        }
    }

    fun toggleFavouriteStatus(allMarketsMarketModel: AllMarketsMarketModel) {
        viewModelScope.launch(Dispatchers.IO) {
            marketRepository.updateMarketModel(
                allMarketsMarketModel.copy(isFavourite = !allMarketsMarketModel.isFavourite).toMarketModel()
            )
        }
    }

}