package com.safari.traderbot.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ActivityTradeOrderBinding
import com.safari.traderbot.di.Provider
import com.safari.traderbot.rest.StockApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
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

        marketAdapter = MarketAdapter(
            binding.root.context.resources.getColor(R.color.grey),
            binding.root.context.resources.getColor(R.color.green),
        )
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
            lifecycleScope.launch(Dispatchers.IO) {

                try {
                    withContext(Dispatchers.Main) {
                        binding.pgLoading.visibility = View.VISIBLE
                    }
                    val putMarkerOrderResponse = Provider.getStockApi().putMarketOrder(
                        marketAdapter.selectedMarket.second,
                        StockApi.ORDER_TYPE.getTypeByString(binding.typeDropDown.selectedItem.toString()),
                        binding.amount.text.toString().toFloat()
                    )

                    withContext(Dispatchers.Main) {
                        binding.pgLoading.visibility = View.GONE
                        binding.tvResult.visibility = View.VISIBLE
                        binding.tvResult.text = putMarkerOrderResponse
                    }

                    Log.d("putmarkerorder", putMarkerOrderResponse)

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }

        binding.svMarket.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
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

}