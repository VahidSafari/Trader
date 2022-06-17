package com.safari.traderbot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ActivityCoinListBinding
import com.safari.traderbot.di.Provider
import com.safari.traderbot.model.Market

class CoinListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinListBinding

    private lateinit var coinAdapter: CoinAdapter

    private lateinit var fragmentStateAdapter: MarketListFragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        coinAdapter = CoinAdapter()
        binding.rvCoins.adapter = coinAdapter

        lifecycleScope.launchWhenCreated {
            binding.pgLoading.visibility = View.VISIBLE
            val marketList = Provider.getCoinexService().getMarketList()
            //TODO: replace real isFavourite with false
            coinAdapter.submitList(marketList.data?.mapIndexed { index, str -> Market(index, str, false) })
            binding.pgLoading.visibility = View.GONE
            Log.d("coinList", marketList.toString())
        }


    }
}