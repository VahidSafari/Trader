package com.safari.traderbot.service

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.safari.traderbot.R
import com.safari.traderbot.data.MarketDataSource
import com.safari.traderbot.data.MarketMockDataSourceImpl
import com.safari.traderbot.ui.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TrailingStopService : Service() {

    companion object {
        private const val STOP_PERCENT = 0.8f
        private const val NOTIFICATION_ID = 1
    }

    private var maximumSeenPrice: Double = 0.0

    private val marketDataSource: MarketDataSource = MarketMockDataSourceImpl()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        showForegroundNotification("Trader App Is Running")
        GlobalScope.launch {
            marketDataSource.getMarketInfo().collect { newTick ->
                Log.d("trailingStopStrategy", "$newTick")
                when {
                    newTick.closePrice > maximumSeenPrice -> {
                        maximumSeenPrice = newTick.closePrice
                        Log.d(
                            "trailingStopStrategy",
                            "going up -> new close price: ${newTick.closePrice}, maximum seen price: $maximumSeenPrice"
                        )
                    }
                    newTick.closePrice < maximumSeenPrice * STOP_PERCENT -> {
                        Log.d(
                            "trailingStopStrategy",
                            "stop exceeded -> new close price: ${newTick.closePrice}, maximum seen price: $maximumSeenPrice"
                        )
//                        TODO("PUT MARKER ORDER")
                    }
                    else -> {
                        Log.d(
                            "trailingStopStrategy",
                            "going down -> new close price: ${newTick.closePrice}, maximum seen price: $maximumSeenPrice"
                        )
                    }
                }
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
    private fun createNotificationChannel(channelId: String, channelName: String): String? {
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

}