package com.safari.traderbot.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.safari.traderbot.R
import com.safari.traderbot.databinding.ActivityCoinListBinding
import com.safari.traderbot.ui.adapter.FavouriteAdapter
import com.safari.traderbot.ui.adapter.MarketListFragmentStateAdapter
import com.safari.traderbot.ui.fragment.AllMarketsFragment
import com.safari.traderbot.ui.fragment.FavouriteFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoinListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinListBinding

    private lateinit var marketListFragmentStateAdapter: MarketListFragmentStateAdapter

    private lateinit var tabLayoutMediator: TabLayoutMediator

    private lateinit var favouriteAdapter: FavouriteAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabLayoutMediator =
            TabLayoutMediator(binding.tlMarketLists, binding.vpMarketLists) { tab, position ->
                tab.text =
                    when (position) {
                        0 -> getString(R.string.all_markets)
                        1 -> getString(R.string.favourites)
                        2 -> getString(R.string.trailing_stop_loss)
                        else -> ""
                    }
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
    }
}