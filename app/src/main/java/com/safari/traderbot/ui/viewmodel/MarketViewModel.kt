package com.safari.traderbot.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safari.traderbot.data.MarketRepository
import com.safari.traderbot.entity.MarketEntity
import com.safari.traderbot.model.*
import com.safari.traderbot.model.market.MarketDetail
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.utils.Event
import com.safari.traderbot.utils.launchPeriodic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    companion object {
        const val MIN_AMOUNT_UNINITIALIZED = -1.1
        const val GET_ALL_MARKETS_PHRASE = ""
        const val INTERVAL_TO_FETCH_TICKERS_IN_MILLIS = 4000L
    }

    val marketsLiveData = MutableLiveData<List<MarketEntity>>()
    val searchResultLiveData = MediatorLiveData<List<MarketEntity>>()
    val marketOrderResult = MutableLiveData<GenericResponse<MarketOrderResponse>>()
    val favouriteLiveData = MediatorLiveData<List<MarketEntity>>()
    val searchPhraseLiveData = MutableLiveData<String>()
    val snackBarLiveData = MutableLiveData<String>()
    val updateAmountErrorTriggerLiveData = MutableLiveData<Any>()
    val openTradePageTriggerLiveData = MutableLiveData<Event<String>>()
    var fetchingMarketDataJob: Job? = null

    var minAmount = MutableLiveData(MIN_AMOUNT_UNINITIALIZED)

    init {

        favouriteLiveData.addSource(marketsLiveData) {
            val onlyFavouriteMarkets = it.filter { market -> market.isFavourite }
            favouriteLiveData.value = onlyFavouriteMarkets
        }

        searchResultLiveData.addSource(marketsLiveData) { marketList ->
            val searchResult: List<MarketEntity>? =
                if (searchPhraseLiveData.value.isNullOrBlank()) {
                    marketList
                } else {
                    marketList?.filter {
                        it.name.lowercase().contains(searchPhraseLiveData.value!!)
                    }
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

    fun toggleFavouriteStatus(favouriteMarketModel: FavouriteMarketModel) {
        viewModelScope.launch(Dispatchers.IO) {
            marketRepository.updateMarketModel(
                favouriteMarketModel.copy(isFavourite = !favouriteMarketModel.isFavourite)
                    .toMarketEntity()
            )
        }
    }

    fun getMarketsLivedata() {
        Log.d("flowtest", "get market in view model called!")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val marketListLiveData = marketRepository.getMarketList()
                withContext(Dispatchers.Main) {
                    marketListLiveData.observeForever {
                        marketsLiveData.value = it ?: listOf()
                    }
                }
            } catch (e: Exception) {
                snackBarLiveData.postValue(
                    when (e) {
                        is ConnectException -> "connection problem"
                        else -> "an error occured while fetching market list"
                    }
                )
            }
        }
    }

    suspend fun getMarketDetail(marketName: String): GenericResponse<MarketDetail?> {
        return marketRepository.getSingleMarketInfo(marketName)
    }

    fun getMinAmount(marketName: String, orderType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val marketDetail = getMarketDetail(marketName)
            val singleMarketStatistics = marketRepository.getSingleMarketStatistics(marketName)
            val selectedMarketOrderTickerValue = when (orderType.lowercase()) {
                ORDER_TYPE_BUY -> {
                    singleMarketStatistics.data.tickerDetails.buy!!.toDouble()
                }
                ORDER_TYPE_SELL -> {
                    singleMarketStatistics.data.tickerDetails.sell!!.toDouble()

                }
                else -> {
                    0.0
                }
            }

            val minAmountInTargetMarket = marketDetail.data?.minAmount?.toDouble()
                ?.times(selectedMarketOrderTickerValue)

            minAmount.postValue(minAmountInTargetMarket ?: MIN_AMOUNT_UNINITIALIZED)
            updateAmountErrorTriggerLiveData.postValue(Any())
        }
    }

    suspend fun getSingleMarketStatistics(marketName: String) {
        marketRepository.getSingleMarketStatistics(marketName)
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
                    .toMarketEntity()
            )
        }
    }

    fun startFetchingPriceUpdateOfFavouriteMarkets() {
        stopFetchingMarketData("starting a new job to fetch market data")
        fetchingMarketDataJob =
            viewModelScope.launchPeriodic(INTERVAL_TO_FETCH_TICKERS_IN_MILLIS, Dispatchers.IO) {
                marketRepository.fetchPriceUpdateOfFavouriteMarkets()
            }
    }

    fun stopFetchingMarketData(message: String) {
        fetchingMarketDataJob?.cancel(CancellationException(message))
    }

    fun openTradePage(marketName: String) {
        openTradePageTriggerLiveData.postValue(Event(marketName))
    }

}