package com.safari.traderbot.ui

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safari.traderbot.data.MarketRepository
import com.safari.traderbot.entity.MarketEntity
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    companion object {
        const val MIN_AMOUNT_UNINITIALIZED = -1.1
        const val GET_ALL_MARKETS_PHRASE = ""
    }

    val marketsLiveData = MutableLiveData<List<MarketEntity>>()
    val searchResultLiveData = MediatorLiveData<List<MarketEntity>>()
    val marketOrderResult = MutableLiveData<GenericResponse<MarketOrderResponse>>()
    val favouriteLiveData = MediatorLiveData<List<MarketEntity>>()
    val searchPhraseLiveData = MutableLiveData<String>()

    var minAmount = MutableLiveData(MIN_AMOUNT_UNINITIALIZED)

    init {

        favouriteLiveData.addSource(marketsLiveData) {
            val onlyFavouriteMarkets = it.filter { market -> market.isFavourite }
            favouriteLiveData.value = onlyFavouriteMarkets
        }

        searchResultLiveData.addSource(marketsLiveData) { marketList ->
            val searchResult: List<MarketEntity>? = if (searchPhraseLiveData.value.isNullOrBlank()) {
                marketList
            } else {
                marketList?.filter { it.name.lowercase().contains(searchPhraseLiveData.value!!) }
            }
            searchResultLiveData.value = searchResult ?: listOf()
        }

        searchResultLiveData.addSource(searchPhraseLiveData) { searchPhrase ->
            val searchResult: List<MarketEntity>? = if (searchPhrase.isNullOrBlank()) {
                marketsLiveData.value
            } else {
                marketsLiveData.value?.filter { it.name.lowercase().contains(searchPhrase) }
            }
            searchResultLiveData.value = searchResult ?: listOf()
        }

    }

    fun getMarketsLivedata() {
        Log.d("flowtest", "get market in view model called!")
        viewModelScope.launch(Dispatchers.IO) {
            val marketListLiveData = marketRepository.getMarketList()
            withContext(Dispatchers.Main) {
                marketListLiveData.observeForever {
                marketsLiveData.postValue(it ?: listOf())
            }
            }
        }
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
                allMarketsMarketModel.copy(isFavourite = !allMarketsMarketModel.isFavourite)
                    .toMarketModel()
            )
        }
    }

}