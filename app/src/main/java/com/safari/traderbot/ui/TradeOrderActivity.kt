package com.safari.traderbot.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ActivityTradeOrderBinding
import com.safari.traderbot.di.Provider
import com.safari.traderbot.model.marketorder.MarkerOrderParam
import com.safari.traderbot.network.CoinexStatusCode
import com.safari.traderbot.rest.StockApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TradeOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeOrderBinding

    private lateinit var marketAdapter: MarketAdapter

    private val marketViewModel: MarketViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTradeOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        marketAdapter = MarketAdapter()
        binding.rvMarkets.adapter = marketAdapter

        lifecycleScope.launchWhenCreated {
            Log.d("flowtest", "market list received!")
            marketViewModel.getMarkets()
        }

        marketViewModel.markets.observe(this@TradeOrderActivity) {
            marketAdapter.submitList(it)
        }

        marketViewModel.searchResult.observe(this@TradeOrderActivity) {
            Log.d("searchresult", "observed $it")
            marketAdapter.submitList(it)
        }

        binding.typeDropDown.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            StockApi.ORDER_TYPE.values()
        )

        binding.submitOrderButton.setOnClickListener {

            setAmountError(null)

            lifecycleScope.launch(Dispatchers.IO) {

                try {
                    withContext(Dispatchers.Main) {
                        binding.pgLoading.visibility = View.VISIBLE
                    }

                    val submitResponse = Provider.getCoinexService().submitMarketOrder(
                        MarkerOrderParam(
                            marketAdapter.selectedMarket.third,
                            binding.typeDropDown.selectedItem.toString(),
                            binding.amount.text.toString(),
                            System.currentTimeMillis().toString()
                        )
                    )

                    when (submitResponse.code) {
                        CoinexStatusCode.BELOW_THE_MINIMUM_LIMIT_FOR_BUYING_OR_SELLING -> {
                            val marketDetail =
                                marketViewModel.getMarketDetail(marketAdapter.selectedMarket.third)
                            setAmountError("min amount is ${marketDetail.data?.minAmount}")
                        }
                        CoinexStatusCode.SUCCEEDED -> {

                        }
                    }

                    Log.d("ommaree", submitResponse.message)
                    Log.d("ommaree", submitResponse.data.toString())

                    withContext(Dispatchers.Main) {
                        binding.pgLoading.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            submitResponse.message,
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction(R.string.close) { dismiss() }
                            show()
                        }
                    }

                    Log.d("putmarkerorder", submitResponse.data.toString())

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }

        binding.svMarket.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    marketViewModel.searchInMarkets(newText)
                } else {
                    marketViewModel.getLastFetchedAllMarkets()
                }
                Log.d("searchresult", "searched for $newText ")
                return false
            }

        })
        binding.svMarket.setOnCloseListener {
            marketViewModel.getLastFetchedAllMarkets()
            return@setOnCloseListener false
        }

    }

    fun setAmountError(errorMessage: String?) {
        binding.amount.error = errorMessage
    }

}