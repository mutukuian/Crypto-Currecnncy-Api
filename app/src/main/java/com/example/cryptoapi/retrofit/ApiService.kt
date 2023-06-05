package com.example.cryptoapi.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    //vs_currency
    //URL= https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=100&page=1&sparkline=true&locale=en

    @GET("coins/markets?sparkline=true")
    suspend fun getCoinsList(@Query("vs_currency") vs_currency : String):Response<ResponseList>


}