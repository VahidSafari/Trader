package com.safari.traderbot.service

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class TrailingStopViewModel @Inject constructor(): ViewModel() {

    // default value is used to prevent null pointer crashes
    val runningTSLs = MutableLiveData<ConcurrentHashMap<String, MarketTrailingStopModel>>(
        ConcurrentHashMap()
    )

    val marketsToRemove = mutableListOf<String>()

    fun updateSingleTrailingStopModel(
        marketName: String,
        newMarketTrailingStopModel: MarketTrailingStopModel
    ) {
        val marketTrailingStopModels = runningTSLs.value!!
        marketTrailingStopModels[marketName] = newMarketTrailingStopModel
        runningTSLs.value = ConcurrentHashMap(marketTrailingStopModels)
    }

    fun removeSingleTrailingStopModel(
        marketName: String
    ) {
        val marketTrailingStopModels = runningTSLs.value!!
        marketTrailingStopModels.remove(marketName)
        runningTSLs.value = ConcurrentHashMap(marketTrailingStopModels)
    }

}