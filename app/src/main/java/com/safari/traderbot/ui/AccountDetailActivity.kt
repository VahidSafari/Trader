package com.safari.traderbot.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.R
import com.safari.traderbot.di.Provider
import java.util.*

class AccountDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_detail)

        lifecycleScope.launchWhenCreated {
            val balanceInfo = Provider.getCoinexService().getBalanceInfo(Date().time)
            Log.d("balanceInfo", balanceInfo.toString())
            Toast.makeText(this@AccountDetailActivity, balanceInfo.toString(), Toast.LENGTH_LONG).show()
        }

    }
}