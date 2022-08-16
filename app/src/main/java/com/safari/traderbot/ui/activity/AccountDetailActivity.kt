package com.safari.traderbot.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.databinding.ActivityAccountDetailBinding
import com.safari.traderbot.network.CoinexService
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AccountDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountDetailBinding

    @Inject
    lateinit var coinexService: CoinexService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            binding.pgLoading.visibility = View.VISIBLE
            val balanceInfo = coinexService.getBalanceInfo(Date().time)
            binding.pgLoading.visibility = View.GONE
            binding.tvBalance.text = balanceInfo.data.toString()
        }



    }
}