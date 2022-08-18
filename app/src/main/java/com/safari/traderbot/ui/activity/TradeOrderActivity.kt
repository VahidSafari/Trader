package com.safari.traderbot.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.safari.traderbot.R
import com.safari.traderbot.data.AccountDataSource
import com.safari.traderbot.data.MarketRepository
import com.safari.traderbot.databinding.ActivityTradeOrderBinding
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.balanceinfo.BalanceInfo
import com.safari.traderbot.model.balanceinfo.MarketBalanceInfo
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketorder.MarketOrderResponse
import com.safari.traderbot.network.CoinexStatusCode
import com.safari.traderbot.rest.StockApi
import com.safari.traderbot.service.TrailingStopService
import com.safari.traderbot.service.TrailingStopService.Companion.TSL_SERVICE_MARKET_NAME_PARAM
import com.safari.traderbot.service.TrailingStopService.Companion.TSL_SERVICE_MARKET_STOP_PERCENT_PARAM
import com.safari.traderbot.service.TrailingStopService.Companion.extractCurrentMarketName
import com.safari.traderbot.ui.viewmodel.MarketViewModel
import com.safari.traderbot.utils.readInstanceProperty
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TradeOrderActivity : AppCompatActivity() {

    companion object {
        const val TRADE_ORDER_ACTIVITY_MARKET_NAME_PARAM = "TradeOrderActivityMarketNameParam"
    }

    private lateinit var binding: ActivityTradeOrderBinding

    private val marketViewModel: MarketViewModel by viewModels()

    @Inject
    lateinit var accountDataSource: AccountDataSource

    @Inject
    lateinit var marketRepository: MarketRepository

    private enum class OrderMainType(val value: String) {
        MARKET("Market"),
        TRAILING_STOP_LOSS("Trailing Stop Loss");

        companion object {
            fun getStringList() = listOf(MARKET.value, TRAILING_STOP_LOSS.value)
        }

    }

    private lateinit var orderMainType: OrderMainType

    private lateinit var marketName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        marketName = intent.getStringExtra(TRADE_ORDER_ACTIVITY_MARKET_NAME_PARAM)!!

        binding = ActivityTradeOrderBinding.inflate(layoutInflater).apply {
            this.marketName = this@TradeOrderActivity.marketName
        }
        setContentView(binding.root)

        initTypeDropDown()
        initTradeTypeDropDown()

        setListeners()

        marketViewModel.minAmount.observe(this, ::onMinAmountChanged)

        marketViewModel.snackBarLiveData.observe(this, ::showSnackBar)

        marketViewModel.marketOrderResult.observe(this, ::onNewMarketResponseReceived)

        marketViewModel.updateAmountErrorTriggerLiveData.observe(this) { updateAmountError() }

        lifecycleScope.launchWhenCreated { marketViewModel.getMarketsLivedata() }

    }

    private fun initTradeTypeDropDown() {
        binding.tradeTypeDropDown.adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            OrderMainType.getStringList()
        )
        binding.tradeTypeDropDown.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (parent?.getItemAtPosition(position).toString()) {
                        OrderMainType.MARKET.value -> {
                            showMarketOrderViews()
                        }
                        OrderMainType.TRAILING_STOP_LOSS.value -> {
                            showTrailingStopLossViews()
                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

    }

    private fun showTrailingStopLossViews() {
        binding.typeDropDown.visibility = View.GONE
        binding.tilAmount.visibility = View.GONE
        binding.submitOrderButton.visibility = View.GONE

        binding.tilStopPercent.visibility = View.VISIBLE
        binding.submitTrailingStopOrderButton.visibility = View.VISIBLE
    }

    private fun showMarketOrderViews() {
        binding.tilStopPercent.visibility = View.GONE
        binding.submitTrailingStopOrderButton.visibility = View.GONE

        binding.typeDropDown.visibility = View.VISIBLE
        binding.tilAmount.visibility = View.VISIBLE
        binding.submitOrderButton.visibility = View.VISIBLE
    }

    private fun initTypeDropDown() {
        binding.typeDropDown.adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            StockApi.ORDER_TYPE.getStringList()
        )
    }

    private fun onMinAmountChanged(minAmount: Double) {
        if (minAmount == MarketViewModel.MIN_AMOUNT_UNINITIALIZED) {
            resetAmountError()
        }
    }

    private fun setListeners() {
        binding.submitOrderButton.setOnClickListener { onSubmitOrderClicked() }
        binding.submitTrailingStopOrderButton.setOnClickListener { onTrailingStopButtonClicked() }
        setAmountListener()
    }

    private fun onNewMarketResponseReceived(submitResponse: GenericResponse<MarketOrderResponse>) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (submitResponse.code) {
                CoinexStatusCode.BELOW_THE_MINIMUM_LIMIT_FOR_BUYING_OR_SELLING -> {
                    try {
                        marketViewModel.getMinAmount(
                            binding.tvMarketName.text.toString(),
                            binding.typeDropDown.selectedItem.toString()
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {
                    showSnackBar(submitResponse.message)
                }

            }

            Log.d("putmarkerorder", submitResponse.data.toString())

            enableOrderButtonsAndHideLoading()
        }

    }

    private fun enableOrderButtonsAndHideLoading() {
        binding.pgLoading.visibility = View.GONE
        binding.submitOrderButton.isEnabled = true
        binding.submitTrailingStopOrderButton.isEnabled = true
    }

    private fun disableOrderButtonsAndShowLoading() {
        binding.pgLoading.visibility = View.VISIBLE
        binding.submitOrderButton.isEnabled = false
        binding.submitTrailingStopOrderButton.isEnabled = false

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

        disableOrderButtonsAndShowLoading()

        orderMainType = OrderMainType.MARKET

        Log.d("submitClick", "submit normal order clicked!")

        resetAmountError()

        lifecycleScope.launch(Dispatchers.Main) {
            marketViewModel.submitMarketOrder(
                MarketOrderParamView(
                    marketName = marketName,
                    orderType = binding.typeDropDown.selectedItem.toString(),
                    selectedMarketOrderAmount = binding.amount.text.toString().toDouble(),
                    tonce = System.currentTimeMillis()
                )
            )
        }
    }

    private fun onTrailingStopButtonClicked() {
        orderMainType = OrderMainType.TRAILING_STOP_LOSS

        Log.d("submitClick", "submit trailing stop loss order clicked!")

        resetAmountError()

        lifecycleScope.launch(Dispatchers.IO) {

            try {

                val balanceResponse = accountDataSource.getBalanceInfo().data

                val balanceInfo: MarketBalanceInfo? =
                    readInstanceProperty<MarketBalanceInfo, BalanceInfo>(
                        balanceResponse!!,
                        extractCurrentMarketName(binding.marketName!!)
                    )

                val singleMarketStatistics = marketRepository.getSingleMarketStatistics(marketName).data

                val marketDetail = marketViewModel.getMarketDetail(marketName)

                if (balanceInfo != null &&
                    singleMarketStatistics != null &&
                    balanceInfo.available.toDouble() >= marketDetail.data?.minAmount!!.toDouble()
                ) {
                    startTrailingStopService(binding.tvStopPercent.text.toString().toDouble())
                } else {
                    showSnackBar("not enough balance! required: ${singleMarketStatistics!!}")
                }

            } catch (e: java.lang.NumberFormatException) {
                showSnackBar("Stop percent is wrong. Enter a number in [0-100] range.")
                e.printStackTrace()
            } catch (e: Exception) {
                showSnackBar("Unknown exception occurred. try again!")
                e.printStackTrace()
            }

        }

    }

    private fun startTrailingStopService(stopPercent: Double) {
        val intent = Intent(applicationContext, TrailingStopService::class.java)
        intent.putExtra(TSL_SERVICE_MARKET_NAME_PARAM, marketName)
        intent.putExtra(
            TSL_SERVICE_MARKET_STOP_PERCENT_PARAM,
            stopPercent / 100
        ) /* divided by 100 to get percent */
        startService(intent)
        Log.d("trailingStopStrategy", "service started")
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

}