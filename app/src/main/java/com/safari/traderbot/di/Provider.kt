package com.safari.traderbot.di

import com.google.gson.Gson
import com.safari.traderbot.network.CoinexService
import com.safari.traderbot.rest.StockApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Provider {

    companion object {

        private var okHttpClient: OkHttpClient? = null

        private var retrofit: Retrofit? = null

        private var coinexService: CoinexService? = null

        private var stockApi: StockApi? = null

        private var gson: Gson? = null

        const val BASE_URL = "https://api.coinex.com/v1/"

        const val ACCESS_ID_HEADER_KEY = "access_id"

        const val SECRET_KEY_HEADER_KEY = "secret_key"

        const val ACCESS_ID_VALUE = "D5087D3C965E45D8AA018239AB592198"

        const val SECRET_KEY_VALUE = "6F2893545D5702B3DE4F57BCB5456AAC3828628418571996"

        fun getOkhttpClient(): OkHttpClient {
            if (okHttpClient == null) {

                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build()
            }
            return okHttpClient!!
        }

        fun getRetrofit(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkhttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

        fun getCoinexService(): CoinexService {
            if (coinexService == null) {
                coinexService = getRetrofit().create(CoinexService::class.java)
            }
            return coinexService!!
        }

        fun getStockApi(): StockApi {
            if (stockApi == null) {
                stockApi = StockApi(BASE_URL, ACCESS_ID_VALUE, SECRET_KEY_VALUE)
            }
            return stockApi!!
        }

        fun getGson(): Gson = gson ?: Gson().apply { gson = this }

    }

}