package com.safari.traderbot.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.marketorder.MarkerOrderParam
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.network.CoinexStatusCode
import com.safari.traderbot.rest.StockApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.NumberFormatException

class TradeOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeOrderBinding

    private lateinit var marketAdapter: MarketAdapter

    private val marketViewModel: MarketViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTradeOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        marketAdapter = MarketAdapter(marketViewModel)
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

        marketViewModel.minAmount.observe(this) {
            if (it == MarketViewModel.MIN_AMOUNT_UNINITIALIZED) {
                resetAmountError()
            }
        }

        binding.typeDropDown.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            StockApi.ORDER_TYPE.values()
        )

        binding.submitOrderButton.setOnClickListener {

            Log.d("submitClick", "clicked!")

            resetAmountError()

            lifecycleScope.launch(Dispatchers.Main) {

                try {
                    binding.pgLoading.visibility = View.VISIBLE

                    val submitResponse = withContext(Dispatchers.IO) {
                        return@withContext Provider.getCoinexService().submitMarketOrder(
                            MarkerOrderParam(
                                marketAdapter.selectedMarket.third,
                                binding.typeDropDown.selectedItem.toString(),
                                binding.amount.text.toString(),
                                System.currentTimeMillis().toString()
                            )
                        )
                    }

                    when (submitResponse.code) {
                        CoinexStatusCode.BELOW_THE_MINIMUM_LIMIT_FOR_BUYING_OR_SELLING -> {
                            val marketDetail = withContext(Dispatchers.IO) {
                                return@withContext marketViewModel.getMarketDetail(marketAdapter.selectedMarket.third)
                            }
                            marketViewModel.minAmount.value =
                                marketDetail.data?.minAmount?.toDouble()
                                    ?: MarketViewModel.MIN_AMOUNT_UNINITIALIZED

                            updateAmountError()
                        }
                        else -> {
                            Log.d("ommaree", submitResponse.message)
                            Log.d("ommaree", submitResponse.data.toString())

                            Snackbar.make(
                                binding.root,
                                submitResponse.message,
                                Snackbar.LENGTH_LONG
                            ).apply {
                                setAction(R.string.close) { dismiss() }
                                show()
                            }
                        }

                    }

                    Log.d("putmarkerorder", submitResponse.data.toString())

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    binding.pgLoading.visibility = View.GONE
                }

            }
        }

        setSearchListeners()
        setAmountListener()

    }

    private fun setAmountListener() {

        binding.amount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val shouldShowMinAmount =
                        !((marketViewModel.minAmount.value != MarketViewModel.MIN_AMOUNT_UNINITIALIZED &&
                                marketViewModel.minAmount.value!! <= s.toString().toDouble()) ||
                                marketViewModel.minAmount.value == MarketViewModel.MIN_AMOUNT_UNINITIALIZED)

                    binding.tilAmount.isErrorEnabled = shouldShowMinAmount

                    if (shouldShowMinAmount) {
                        updateAmountError()
                    }

                    Log.d(
                        "setAmountListener",
                        "${
                            !(marketViewModel.minAmount.value != MarketViewModel.MIN_AMOUNT_UNINITIALIZED &&
                                    marketViewModel.minAmount.value!! < s.toString().toDouble())
                        }"
                    )
                } catch (exception: NumberFormatException) {
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun updateAmountError() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.tilAmount.isErrorEnabled = true
            binding.tilAmount.error =
                "min amount is ${marketViewModel.minAmount.value!!.toBigDecimal().toPlainString()}"
        }
    }

    private fun resetAmountError() {
        binding.tilAmount.isErrorEnabled = false
    }

    private fun setSearchListeners() {
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

}