package com.aulaandroid.pokedex.service

import com.aulaandroid.pokedex.model.Pokemon
import com.aulaandroid.pokedex.model.PokemonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonService {
    @GET("pokemon")
    fun listPokemon(@Query("limit") limit: Int = 20, @Query("offset") offset: Int = 0): Call<PokemonResponse>

    @GET("pokemon/{id}")
    fun getPokemon(@Path("id") id: String): Call<Pokemon>
}
