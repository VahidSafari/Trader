package com.safari.traderbot.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.safari.traderbot.databinding.ActivityMainBinding

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