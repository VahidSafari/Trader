package com.safari.traderbot.ui

import android.content.Intent
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
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.network.CoinexStatusCode
import com.safari.traderbot.rest.StockApi
import com.safari.traderbot.service.TrailingStopService
import com.safari.traderbot.service.TrailingStopService.Companion.MARKET_NAME_PARAM
import com.safari.traderbot.utils.isServiceRunning
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class TradeOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeOrderBinding

    private lateinit var marketAdapter: MarketAdapter

    private val marketViewModel: MarketViewModel by viewModels()

    private enum class OrderMainType {
        NORMAL,
        TSL
    }

    private lateinit var orderMainType: OrderMainType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTradeOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        marketAdapter = MarketAdapter(marketViewModel)
        binding.rvMarkets.adapter = marketAdapter

        setListeners()

        lifecycleScope.launchWhenCreated { marketViewModel.getMarketsLivedata() }

        marketViewModel.marketsLiveData.observe(this@TradeOrderActivity) {
            marketAdapter.submitList(it)
        }

        marketViewModel.searchResultLiveData.observe(this@TradeOrderActivity) {
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

        marketViewModel.marketOrderResult.observe(this) { submitResponse ->
            onNewMarketResponseReceived(submitResponse)
        }

        marketViewModel.snackBarLiveData.observe(this) {
           showSnackBar(it)
        }

    }

    private fun setListeners() {
        binding.submitOrderButton.setOnClickListener { onSubmitOrderClicked() }
        binding.submitTrailingStopOrderButton.setOnClickListener { onTrailingStopButtonClicked() }
        setSearchListeners()
        setAmountListener()
    }

    private fun onNewMarketResponseReceived(submitResponse: GenericResponse<MarketOrderResponse>) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (submitResponse.code) {
                CoinexStatusCode.BELOW_THE_MINIMUM_LIMIT_FOR_BUYING_OR_SELLING -> {
                    val marketDetail = withContext(Dispatchers.IO) {
                        return@withContext marketViewModel.getMarketDetail(
                            marketAdapter.selectedMarket.name
                        )
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

                    if (submitResponse.isSuccessful()) {
                        when(orderMainType) {
                            OrderMainType.TSL -> {
                                startTSL()
                            }
                            else -> {}
                        }
                    }

                }

            }

            Log.d("putmarkerorder", submitResponse.data.toString())

            binding.pgLoading.visibility = View.GONE
            binding.submitOrderButton.isEnabled = true
            binding.submitTrailingStopOrderButton.isEnabled = true
        }

    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(R.string.close) { dismiss() }
            show()
        }
    }

    private fun onSubmitOrderClicked() {
        binding.submitOrderButton.isEnabled = false
        binding.submitTrailingStopOrderButton.isEnabled = false
        orderMainType = OrderMainType.NORMAL

        Log.d("submitClick", "submit normal order clicked!")

        resetAmountError()

        lifecycleScope.launch(Dispatchers.Main) {

            binding.pgLoading.visibility = View.VISIBLE

            marketViewModel.submitMarketOrder(
                MarketOrderParamView(
                    marketName = marketAdapter.selectedMarket.name,
                    orderType = binding.typeDropDown.selectedItem.toString(),
                    selectedMarketOrderAmount = binding.amount.text.toString().toDouble(),
                    tonce = System.currentTimeMillis()
                )
            )

        }
    }

    private fun onTrailingStopButtonClicked() {
        binding.submitTrailingStopOrderButton.isEnabled = false
        binding.submitOrderButton.isEnabled = false
        orderMainType = OrderMainType.TSL

        Log.d("submitClick", "submit trailing stop loss order clicked!")

        resetAmountError()

        lifecycleScope.launch(Dispatchers.Main) {

            binding.pgLoading.visibility = View.VISIBLE

            marketViewModel.submitMarketOrder(
                MarketOrderParamView(
                    marketName = marketAdapter.selectedMarket.name,
                    orderType = binding.typeDropDown.selectedItem.toString(),
                    selectedMarketOrderAmount = binding.amount.text.toString().toDouble(),
                    tonce = System.currentTimeMillis()
                )
            )

        }
    }

    private fun startTSL() {
        if (!isServiceRunning(this@TradeOrderActivity, TrailingStopService::class.java)) {
            val intent = Intent(applicationContext, TrailingStopService::class.java)
            intent.putExtra(MARKET_NAME_PARAM, marketAdapter.selectedMarket.name)
            startService(intent)
            Log.d("trailingStopStrategy", "service started")
        }
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
            override fun onQueryTextSubmit(searchPhrase: String?): Boolean {
                marketViewModel.searchPhraseLiveData.value = searchPhrase
                return false
            }

            override fun onQueryTextChange(searchPhrase: String?): Boolean {
                marketViewModel.searchPhraseLiveData.value = searchPhrase
                Log.d("searchresult", "searched for $searchPhrase ")
                return false
            }
        })

        binding.svMarket.setOnCloseListener {
            marketViewModel.searchPhraseLiveData.value = MarketViewModel.GET_ALL_MARKETS_PHRASE
            return@setOnCloseListener false
        }
    }

}