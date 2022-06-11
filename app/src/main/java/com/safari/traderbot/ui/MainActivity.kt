package com.safari.traderbot.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.safari.traderbot.databinding.ActivityMainBinding
import com.safari.traderbot.service.TrailingStopService
import com.safari.traderbot.utils.isServiceRunning

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityBinding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        mainActivityBinding.coinListButton.setOnClickListener {
            startActivity(Intent(this, CoinListActivity::class.java))
        }

        mainActivityBinding.accountDetailButton.setOnClickListener {
            startActivity(Intent(this, AccountDetailActivity::class.java))
        }

        mainActivityBinding.tradeOrderButton.setOnClickListener {
            startActivity(Intent(this, TradeOrderActivity::class.java))
        }

    }
}