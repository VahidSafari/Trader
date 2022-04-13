package com.safari.traderbot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.R
import com.safari.traderbot.di.Provider

class CoinListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_list)

        lifecycleScope.launchWhenCreated {
            Provider.getCoinexService().getMarketList()
            Log.d("coinList", Provider.getCoinexService().getMarketList().toString())
        }


    }
}