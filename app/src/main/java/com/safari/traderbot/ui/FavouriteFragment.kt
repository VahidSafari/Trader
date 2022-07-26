package com.safari.traderbot.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.safari.traderbot.databinding.FragmentFavouriteBinding
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

        marketListViewModel = ViewModelProvider(activity as CoinListActivity)[MarketViewModel::class.java]

        favouriteAdapter = FavouriteAdapter(marketListViewModel)
        binding.rvMarkets.adapter = favouriteAdapter

        marketListViewModel.favouriteLiveData.observe(this) {
            favouriteAdapter.submitList(it.map { market -> market.toAllMarketsModel() })
        }



    }
}