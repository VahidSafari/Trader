package com.safari.traderbot.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.databinding.ActivityAccountDetailBinding
import com.safari.traderbot.di.Provider
import java.util.*

class AccountDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            binding.pgLoading.visibility = View.VISIBLE
            val balanceInfo = Provider.getCoinexService().getBalanceInfo(Date().time)
            binding.pgLoading.visibility = View.GONE
            binding.tvBalance.text = balanceInfo.data.toString()
        }

    }
}