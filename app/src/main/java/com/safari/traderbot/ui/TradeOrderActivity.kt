package com.safari.traderbot.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ActivityTradeOrderBinding
import com.safari.traderbot.di.Provider
import com.safari.traderbot.rest.StockApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TradeOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeOrderBinding

    private lateinit var marketAdapter: MarketAdapter

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
            val marketList = Provider.getCoinexService().getMarketList().data
            marketAdapter.submitList(marketList)
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

    }

}