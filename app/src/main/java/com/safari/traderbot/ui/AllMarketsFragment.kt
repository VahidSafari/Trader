package com.safari.traderbot.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.databinding.FragmentAllMarketsBinding

class AllMarketsFragment : Fragment() {

    private val marketListViewModel: MarketViewModel by viewModels()

    private lateinit var marketAdapter: MarketAdapter

    private lateinit var binding: FragmentAllMarketsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAllMarketsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        marketAdapter = MarketAdapter(marketListViewModel)
        binding.rvMarkets.adapter = marketAdapter

        lifecycleScope.launchWhenCreated {
            marketListViewModel.getMarkets()
        }

        marketListViewModel.markets.observe(viewLifecycleOwner) {
            marketAdapter.submitList(it)
        }

        marketListViewModel.searchResult.observe(viewLifecycleOwner) {
            marketAdapter.submitList(it)
        }

    }

}