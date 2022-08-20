package com.safari.traderbot.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.safari.traderbot.R
import com.safari.traderbot.databinding.FragmentTrailingStopLossBinding
import com.safari.traderbot.service.TrailingStopService
import com.safari.traderbot.service.TrailingStopViewModel
import com.safari.traderbot.ui.adapter.TSLAdapter

class TrailingStopLossFragment : Fragment() {

    private lateinit var tslAdapter: TSLAdapter

    private lateinit var binding: FragmentTrailingStopLossBinding

    private lateinit var tslViewModel: TrailingStopViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrailingStopLossBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            tslViewModel = TrailingStopService.getTrailingStopViewModel(activity.application)
            tslAdapter = TSLAdapter()
            binding.tslAdapter = tslAdapter
            tslViewModel.runningTSLs.observe(viewLifecycleOwner) { tslMap ->
                tslAdapter.submitList(
                    tslMap.values.map { tslModel -> tslModel.toTrailingStopLossView() }
                )
            }
        }

    }
}