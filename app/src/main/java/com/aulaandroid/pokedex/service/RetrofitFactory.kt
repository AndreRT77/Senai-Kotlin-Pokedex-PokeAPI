package com.aulaandroid.pokedex.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {

    val BASE_URL = "https://pokeapi.co/api/v2/"
    private val retrofitFactory = Retrofit
        .Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    fun getPokemonService() : PokemonService {
        return retrofitFactory.create(PokemonService::class.java)
    }

}
