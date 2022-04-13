package com.safari.traderbot.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.safari.traderbot.databinding.ActivityTradeOrderBinding
import com.safari.traderbot.di.Provider
import java.util.*

class TradeOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradeOrderBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTradeOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {

            val marketList = Provider.getCoinexService().getMarketList().data

            marketList?.let {
                binding.marketNameDropDown.adapter =
                    ArrayAdapter(
                        this@TradeOrderActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        it
                    )
            }

        }

        binding.typeDropDown.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            OrderType.values()
        )

        binding.submitOrderButton.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                Provider.getCoinexService().submitMarketOrder(
                    market = binding.marketNameDropDown.selectedItem.toString(),
                    type = binding.typeDropDown.selectedItem.toString(),
                    amount = binding.amount.text.toString(),
                    price = binding.price.text.toString(),
                    tonce = Date().time
                )
            }
        }

    }

}