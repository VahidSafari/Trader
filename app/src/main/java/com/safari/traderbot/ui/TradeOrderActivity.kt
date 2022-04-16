package com.safari.traderbot.ui

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.databinding.ActivityTradeOrderBinding
import com.safari.traderbot.di.Provider
import com.safari.traderbot.rest.StockApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TradeOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeOrderBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTradeOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {

            val marketList = Provider.getCoinexService().getMarketList().data

            marketList?.let {
                binding.marketNameDropDown.adapter =
                    ArrayAdapter(
                        this@TradeOrderActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        it
                    )
            }

        }

        binding.typeDropDown.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            StockApi.ORDER_TYPE.values()
        )

        binding.submitOrderButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {

                try {

                    val putMarkerOrderResponse = Provider.getStockApi().putMarketOrder(
                        binding.marketNameDropDown.selectedItem.toString(),
                        StockApi.ORDER_TYPE.getTypeByString(binding.typeDropDown.selectedItem.toString()),
                        binding.amount.text.toString().toFloat()
                    )

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@TradeOrderActivity, putMarkerOrderResponse, Toast.LENGTH_LONG).show()
                    }

                    Log.d("putmarkerorder", putMarkerOrderResponse)

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }

    }

}