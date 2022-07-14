package com.safari.traderbot.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ActivityCoinListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoinListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinListBinding

    private lateinit var marketListFragmentStateAdapter: MarketListFragmentStateAdapter

    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabLayoutMediator =
            TabLayoutMediator(binding.tlMarketLists, binding.vpMarketLists) { tab, position ->
                tab.text =
                    if (position == 0) getString(R.string.all_markets) else getString(R.string.favourites)
            }

        marketListFragmentStateAdapter = MarketListFragmentStateAdapter(
            this,
            listOf(
                AllMarketsFragment(),
                FavouriteFragment()
            )
        )
        binding.vpMarketLists.adapter = marketListFragmentStateAdapter
        tabLayoutMediator.attach()

    }


    override fun onBackPressed() {
        if (binding.vpMarketLists.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.vpMarketLists.currentItem--
        }
        super.onBackPressed()
    }
}