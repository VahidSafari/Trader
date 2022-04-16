package com.safari.traderbot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ActivityCoinListBinding
import com.safari.traderbot.di.Provider

class CoinListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinListBinding

    private lateinit var coinAdapter: CoinAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        coinAdapter = CoinAdapter()
        binding.rvCoins.adapter = coinAdapter

        lifecycleScope.launchWhenCreated {
            val marketList = Provider.getCoinexService().getMarketList()
            coinAdapter.submitList(marketList.data)
            Log.d("coinList", Provider.getCoinexService().getMarketList().toString())
        }


    }
}