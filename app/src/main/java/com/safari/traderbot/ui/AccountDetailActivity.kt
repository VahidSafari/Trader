package com.safari.traderbot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.R
import com.safari.traderbot.di.Provider
import java.util.*

class AccountDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_detail)

        lifecycleScope.launchWhenCreated {
            Provider.getCoinexService().getBalanceInfo(Date().time)
            Log.d("balanceInfo", Provider.getCoinexService().getBalanceInfo(Date().time).toString())
        }

    }
}