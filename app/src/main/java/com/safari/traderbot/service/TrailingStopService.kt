package com.safari.traderbot.service

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.safari.traderbot.R
import com.safari.traderbot.data.AccountDataSource
import com.safari.traderbot.data.MarketDataSource
import com.safari.traderbot.data.MarketDefaultDataSource
import com.safari.traderbot.model.GenericResponse
import com.safari.traderbot.model.ORDER_TYPE_SELL
import com.safari.traderbot.model.balanceinfo.BalanceInfo
import com.safari.traderbot.model.balanceinfo.MarketBalanceInfo
import com.safari.traderbot.model.marketorder.MarketOrderParamView
import com.safari.traderbot.model.marketstatistics.SingleMarketStatisticsResponse
import com.safari.traderbot.ui.MainActivity
import com.safari.traderbot.utils.readInstanceProperty
import kotlinx.coroutines.*


class TrailingStopService : Service() {

    companion object {
        private const val STOP_PERCENT = 0.9999
        private const val NOTIFICATION_ID = 1
        private const val timeFrameInMilliseconds = 4000L
        private const val TAG = "trailingStopStrategy"
    }

    private var maximumSeenPrice: Double = Double.MIN_VALUE
    private var lastSeenPrice: Double = Double.MIN_VALUE

    private val marketDataSource: MarketDataSource = MarketDefaultDataSource()

    private val accountDataSource = AccountDataSource()

    private lateinit var getMarketInfoJob: Job

    //TODO: make market dynamic
    private val currentMarketName = "DOGE"
    private val targetMarketName = "USDT"
    private val currentMarketAndTargetMarketName: String = currentMarketName + targetMarketName

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        showForegroundNotification("Trader App Is Running")

        getMarketInfoInAnInterval()
        marketInfoLiveData.observeForever { newTick ->

            if (newTick.isSuccessful()) {
                val marketStatistics = newTick.data.ticker
                val  buyValue = marketStatistics.buy?.toDouble() ?: Double.MIN_VALUE

                Log.d(
                    TAG,
                    "${
                        when {
                            buyValue < lastSeenPrice -> {
                                "going down "
                            }

                            buyValue > lastSeenPrice -> {
                                "going up "
                            }

                            else -> {
                                "going stable "
                            }
                        }
                    } -> new buy price: $buyValue, last seen price: $lastSeenPrice, ratio: ${buyValue / maximumSeenPrice}"
                )

                Log.d(TAG, "new ticker: $newTick")
                when {

                    buyValue > maximumSeenPrice -> {
                        maximumSeenPrice = buyValue
                        Log.d(
                            TAG,
                            "going upper than max -> new buy price: $buyValue, maximum seen price: $maximumSeenPrice, ratio: ${buyValue / maximumSeenPrice}"
                        )
                    }

                    buyValue < maximumSeenPrice * STOP_PERCENT -> {
                        Log.d(
                            TAG,
                            "stop exceeded -> new buy price: $buyValue, maximum seen price: $maximumSeenPrice, ratio: ${buyValue / maximumSeenPrice}"
                        )
                        GlobalScope.launch(Dispatchers.IO) {

                            val balanceResponse = accountDataSource.getBalanceInfo().data

                            if (balanceResponse != null) {

                                val balanceInfo = readInstanceProperty<MarketBalanceInfo, BalanceInfo>(balanceResponse, currentMarketName)

                                Log.d(TAG, "balance info result: $balanceInfo")

                                val marketOrderResult = marketDataSource.putMarketOrder(
                                    MarketOrderParamView(
                                        marketName = currentMarketAndTargetMarketName,
                                        orderType = ORDER_TYPE_SELL,
                                        selectedMarketOrderAmount = balanceInfo.available.toDouble(),
                                        tonce = System.currentTimeMillis()
                                    )
                                )

                                if (marketOrderResult.isSuccessful()) {
                                    getMarketInfoJob.cancel("COMPLETED THE TRAILING STOP LOSS", Throwable())
                                }

                                Log.d(TAG, "market order result: $marketOrderResult")

                            }

                        }
//
                    }

                    buyValue == maximumSeenPrice -> {
                        Log.d(
                            TAG,
                            "going stable with max -> new buy price: $buyValue, maximum seen price: $maximumSeenPrice"
                        )
                    }

                    else -> {
                        Log.d(
                            TAG,
                            "going downer than max -> new buy price: $buyValue, maximum seen price: $maximumSeenPrice, ratio: ${buyValue / maximumSeenPrice}"
                        )
                    }

                }

                lastSeenPrice = buyValue

                Log.d(TAG, "--------------------------------------------------------------------------------------------------------------------------------")

            }

        }

    }

    private fun showForegroundNotification(contentText: String) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        val showTaskIntent = Intent(applicationContext, MainActivity::class.java)
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

    private val marketInfoLiveData: MutableLiveData<GenericResponse<SingleMarketStatisticsResponse>> = MutableLiveData()

    private fun getMarketInfoInAnInterval() {
        getMarketInfoJob = GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    marketInfoLiveData.postValue(marketDataSource.getSingleMarketStatistics(currentMarketAndTargetMarketName))
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    delay(timeFrameInMilliseconds)
                }
            }
        }
    }


}