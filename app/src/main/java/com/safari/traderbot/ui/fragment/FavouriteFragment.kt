package com.safari.traderbot.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.safari.traderbot.databinding.FragmentFavouriteBinding
import com.safari.traderbot.ui.activity.CoinListActivity
import com.safari.traderbot.ui.viewmodel.MarketViewModel
import com.safari.traderbot.ui.activity.TradeOrderActivity
import com.safari.traderbot.ui.adapter.FavouriteAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteFragment : Fragment() {

    private lateinit var marketListViewModel: MarketViewModel

    private lateinit var favouriteAdapter: FavouriteAdapter

    private lateinit var binding: FragmentFavouriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        marketListViewModel =
            ViewModelProvider(activity as CoinListActivity)[MarketViewModel::class.java]

        val dividerItemDecoration = DividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL)
        binding.rvMarkets.addItemDecoration(dividerItemDecoration)

        favouriteAdapter = FavouriteAdapter(marketListViewModel)
        binding.rvMarkets.adapter = favouriteAdapter

        marketListViewModel.favouriteLiveData.observe(viewLifecycleOwner) { newMarketList ->
            favouriteAdapter.submitList(newMarketList.map { market ->
                market.toFavouriteMarketModel(
                    (favouriteAdapter.currentList.find { it.marketName == market.name }?.price
                        ?: Double.MIN_VALUE) < market.price
                )
            })
            Log.d("favouriteUpdate", newMarketList.toString())
        }

        marketListViewModel.openTradePageTriggerLiveData.observe(viewLifecycleOwner) { marketEvent ->
            marketEvent.getContentIfNotHandled()?.let { market ->
                val tradeOrderActivityIntent = Intent(context, TradeOrderActivity::class.java).apply {
                    putExtra(TradeOrderActivity.TRADE_ORDER_ACTIVITY_MARKET_NAME_PARAM, market)
                }
                startActivity(tradeOrderActivityIntent)
            }
        }

        marketListViewModel.startFetchingPriceUpdateOfFavouriteMarkets()


    }
}