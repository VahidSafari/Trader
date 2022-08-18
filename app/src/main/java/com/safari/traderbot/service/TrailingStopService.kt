package com.safari.traderbot.service

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.safari.traderbot.R
import com.safari.traderbot.data.AccountDataSource
import com.safari.traderbot.data.MarketRepository
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.ORDER_TYPE_SELL
import com.safari.traderbot.model.balanceinfo.BalanceInfo
import com.safari.traderbot.model.balanceinfo.MarketBalanceInfo
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse
import com.safari.traderbot.ui.activity.CoinListActivity
import com.safari.traderbot.utils.readInstanceProperty
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@AndroidEntryPoint
class TrailingStopService : LifecycleService() {

    companion object {
        private const val NOTIFICATION_ID = 1000102
        private const val timeFrameInMilliseconds = 4000L
        private const val TAG = "trailingStopStrategy"

        const val TSL_SERVICE_MARKET_NAME_PARAM = "MARKET_NAME_PARAM"
        const val TSL_SERVICE_MARKET_STOP_PERCENT_PARAM = "MARKET_STOP_PERCENT_PARAM"

        val targetMarkets = listOf(
            "USDT",
            "USDC",
            "BTC",
            "BCH"
        )

        fun extractCurrentMarketName(marketAndTargetMarket: String): String {
            val targetMarketName: String = targetMarkets.first {
                marketAndTargetMarket.endsWith(it)
            }
            return marketAndTargetMarket.dropLast(targetMarketName.length)
        }

        private lateinit var trailingStopViewModel: TrailingStopViewModel

        fun getTrailingStopViewModel(application: Application): TrailingStopViewModel {
            if (!::trailingStopViewModel.isInitialized) {
                trailingStopViewModel =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
                        TrailingStopViewModel::class.java
                    )
            }
            return trailingStopViewModel
        }

    }

    @Inject
    lateinit var marketRepository: MarketRepository

    @Inject
    lateinit var accountDataSource: AccountDataSource

    private var isMaxUpdateNeeded = false

    private val mutex = Mutex()

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand: ")
        extractParamValues(intent?.extras)
        return START_STICKY
    }

    private fun extractParamValues(bundle: Bundle?) {
        val marketNameParam = bundle?.getString(TSL_SERVICE_MARKET_NAME_PARAM)
        val stopPercentParam = bundle?.getDouble(TSL_SERVICE_MARKET_STOP_PERCENT_PARAM)
        if (marketNameParam != null && stopPercentParam != null) {
            if (getTrailingStopViewModel(application).runningTSLs.value?.containsKey(marketNameParam) == true) {
                val newMarketModel =
                    getTrailingStopViewModel(application).runningTSLs.value!![marketNameParam]!!.copy(
                        stopPercent = stopPercentParam
                    )
                getTrailingStopViewModel(application).updateSingleTrailingStopModel(
                    marketNameParam,
                    newMarketModel
                )
            } else {
                val newMarketModel =
                    MarketTrailingStopModel(marketNameParam, stopPercentParam, -1.0, -1.0, -1.0)
                getTrailingStopViewModel(application).updateSingleTrailingStopModel(
                    marketNameParam,
                    newMarketModel
                )
            }
        }
        getTrailingStopViewModel(application).marketsToRemove.remove(marketNameParam)
    }

    override fun onCreate() {
        super.onCreate()

        showForegroundNotification("Trader App Is Running")

        getMarketInfoInAnInterval()
        lifecycleScope.launch {
            marketsInfoStateFlow.collect { newMarketWithTick ->

                if (getTrailingStopViewModel(application).runningTSLs.value.isNullOrEmpty()) return@collect

                // TODO: request for user account balance for the market
                //  and cancel trailing stop loss if the balance is insufficient
                //  for that specific market

                val marketTrailingStopModels =
                    getTrailingStopViewModel(application).runningTSLs.value!!

                if (newMarketWithTick == null) return@collect
                if (marketTrailingStopModels[newMarketWithTick.first] == null) return@collect

                val currentMarketModel = marketTrailingStopModels[newMarketWithTick.first]!!

                isMaxUpdateNeeded = false

                Log.d(TAG, "observed: $newMarketWithTick")

                if (newMarketWithTick.second.isSuccessful()) {
                    val marketStatistics = newMarketWithTick.second.data.tickerDetails
                    val buyValue = marketStatistics.buy?.toDouble() ?: -1.0

                    // <editor-fold desc="Log Ticker">

                    Log.d(TAG, "new ticker: $newMarketWithTick")

                    //</editor-fold>

                    // <editor-fold desc="Log Comparing To Previous Seen Price">
                    Log.d(
                        TAG,
                        "${
                            when {
                                buyValue < currentMarketModel.lastSeenPrice -> {
                                    "going down ↓"
                                }

                                buyValue > currentMarketModel.lastSeenPrice -> {
                                    "going up ↑"
                                }

                                else -> {
                                    "going stable →"
                                }
                            }
                        } -> newBuyPrice: $buyValue, LastSeenPrice: $currentMarketModel.lastSeenPrice, Ratio: ${buyValue / currentMarketModel.maxSeenPrice}"
                    )
                    //</editor-fold>

                    when {

                        buyValue > currentMarketModel.maxSeenPrice -> {
                            isMaxUpdateNeeded = true
                            // <editor-fold desc="Log GoingUpperThanMax">
                            Log.d(
                                TAG,
                                "GoingUpperThanMax ↑ NewBuyPrice: $buyValue, MaxSeenPrice: ${
                                    currentMarketModel.maxSeenPrice.toBigDecimal().toPlainString()
                                }, Ratio: ${buyValue / currentMarketModel.maxSeenPrice}"
                            )
                            //</editor-fold>
                        }

                        buyValue < currentMarketModel.maxSeenPrice * (1 - currentMarketModel.stopPercent) -> {
                            // <editor-fold desc="Log Stopped Trailing Stop Loss">
                            Log.d(
                                TAG,
                                "stopped! -> NewBuyPrice: $buyValue, MaxSeenPrice: ${
                                    currentMarketModel.maxSeenPrice.toBigDecimal().toPlainString()
                                }, Ratio: ${buyValue / currentMarketModel.maxSeenPrice}"
                            )
                            //</editor-fold>
                            lifecycleScope.launch(Dispatchers.IO) {

                                val balanceResponse = accountDataSource.getBalanceInfo().data

                                if (balanceResponse != null) {

                                    val balanceInfo: MarketBalanceInfo? =
                                        readInstanceProperty<MarketBalanceInfo, BalanceInfo>(
                                            balanceResponse,
                                            extractCurrentMarketName(newMarketWithTick.first)
                                        )

                                    // <editor-fold desc="Log BalanceInfo">
                                    Log.d(TAG, "BalanceInfoResult: $balanceInfo")
                                    //</editor-fold>

                                    if (balanceInfo?.available != null) {
                                        val marketOrderResult = marketRepository.putMarketOrder(
                                            MarketOrderParamView(
                                                marketName = newMarketWithTick.first,
                                                orderType = ORDER_TYPE_SELL,
                                                selectedMarketOrderAmount = balanceInfo.available.toDouble(),
                                                tonce = System.currentTimeMillis()
                                            )
                                        )

                                        if (marketOrderResult.isSuccessful()) {
                                            // TODO: just cancel job when all orders are hit by stop
                                        }

                                        Log.d(TAG, "MarketOrderResult: $marketOrderResult")
                                    } else {
                                        Log.d(
                                            TAG,
                                            "Balance is inssuficient! BalanceResponse: $balanceResponse"
                                        )
                                    }

                                }

                            }

                            getTrailingStopViewModel(application).removeSingleTrailingStopModel(
                                newMarketWithTick.first
                            )

                            getTrailingStopViewModel(application).marketsToRemove.add(
                                newMarketWithTick.first
                            )

                        }

                        buyValue == currentMarketModel.maxSeenPrice -> {
                            // <editor-fold desc="Log GoingStableWithMax">
                            Log.d(
                                TAG,
                                "GoingStableWithMax → NewBuyPrice: $buyValue, MaxSeenPrice: ${currentMarketModel.maxSeenPrice}"
                            )
                            //</editor-fold>
                        }

                        else -> {
                            // <editor-fold desc="Log GoingDownerThanMax">
                            Log.d(
                                TAG,
                                "GoingDownerThanMax ↑ NewBuyPrice: $buyValue, MaxSeenPrice: ${currentMarketModel.maxSeenPrice}, Ratio: ${buyValue / currentMarketModel.maxSeenPrice}"
                            )
                            //</editor-fold>
                        }

                    }

                    if (!getTrailingStopViewModel(application).marketsToRemove.contains(
                            newMarketWithTick.first
                        )
                    ) {
                        getTrailingStopViewModel(application).updateSingleTrailingStopModel(
                            newMarketWithTick.first,
                            currentMarketModel.copy(lastSeenPrice = buyValue)
                        )
                    }

                    if (isMaxUpdateNeeded) {
                        getTrailingStopViewModel(application).updateSingleTrailingStopModel(
                            newMarketWithTick.first,
                            currentMarketModel.copy(
                                maxSeenPrice = buyValue,
                                tslPrice = currentMarketModel.maxSeenPrice * (1 - currentMarketModel.stopPercent)
                            )
                        )
                    }

                    Log.d(
                        TAG,
                        "--------------------------------------------------------------------------------------------------------------------------------"
                    )

                }

            }
        }

    }

    private fun showForegroundNotification(contentText: String) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        val showTaskIntent = Intent(applicationContext, CoinListActivity::class.java)
        showTaskIntent.action = Intent.ACTION_MAIN
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            showTaskIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification: Notification.Builder = Notification.Builder(
            applicationContext
        )
            .setContentTitle(getString(R.string.app_name))
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(contentIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notification.setChannelId(
            createNotificationChannel("my_service", "My Background Service")
        )
        startForeground(NOTIFICATION_ID, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
        return channelId
    }

    private val marketsInfoStateFlow: MutableStateFlow<Pair<String, GenericResponse<SingleMarketStatisticsResponse>>?> =
        MutableStateFlow(null)

    @Synchronized
    private fun getMarketInfoInAnInterval() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                Log.d(TAG, getTrailingStopViewModel(application).runningTSLs.value.toString())
                getTrailingStopViewModel(application).runningTSLs.value?.forEach { marketWithStopPercent ->
                    try {
                        marketsInfoStateFlow.emit(
                            Pair(
                                marketWithStopPercent.key,
                                marketRepository.getSingleMarketStatistics(marketWithStopPercent.key)
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        delay(timeFrameInMilliseconds)
                    }
                }
            }
        }
    }


}