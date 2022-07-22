package com.safari.traderbot.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.safari.traderbot.R
import com.safari.traderbot.databinding.FragmentAllMarketsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.net.ConnectException

@AndroidEntryPoint
class AllMarketsFragment : Fragment() {

    private lateinit var marketListViewModel: MarketViewModel

    private lateinit var marketAdapter: AllMarketsAdapter

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

        marketListViewModel = ViewModelProvider(activity as CoinListActivity)[MarketViewModel::class.java]


        val dividerItemDecoration = DividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL)
        binding.rvMarkets.addItemDecoration(dividerItemDecoration)
        marketAdapter = AllMarketsAdapter(marketListViewModel)
        binding.rvMarkets.adapter = marketAdapter

        lifecycleScope.launch(CoroutineExceptionHandler { _, throwable ->
            if (throwable is ConnectException) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.connection_problem),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(R.string.close) { dismiss() }
                    show()
                }
            }
        }) {
            marketListViewModel.getMarketsLivedata()
        }

        marketListViewModel.marketsLiveData.observe(viewLifecycleOwner) {
            marketAdapter.submitList(it.map { market -> market.toAllMarketsModel() })
        }

        marketListViewModel.searchResultLiveData.observe(viewLifecycleOwner) {
            marketAdapter.submitList(it.map { market -> market.toAllMarketsModel() })
        }

        marketListViewModel.snackBarLiveData.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                it,
                Snackbar.LENGTH_LONG
            ).apply {
                setAction(R.string.close) { dismiss() }
                show()
            }
        }

        setSearchListeners()

    }

    private fun setSearchListeners() {
        binding.svMarket.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchPhrase: String?): Boolean {
                marketListViewModel.searchPhraseLiveData.value = searchPhrase
                return false
            }

            override fun onQueryTextChange(searchPhrase: String?): Boolean {
                marketListViewModel.searchPhraseLiveData.value = searchPhrase
                Log.d("searchresult", "searched for $searchPhrase ")
                return false
            }
        })

        binding.svMarket.setOnCloseListener {
            marketListViewModel.searchPhraseLiveData.value = MarketViewModel.GET_ALL_MARKETS_PHRASE
            return@setOnCloseListener false
        }
    }

}