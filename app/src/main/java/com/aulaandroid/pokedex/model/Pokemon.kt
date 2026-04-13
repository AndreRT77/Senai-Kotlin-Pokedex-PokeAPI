package com.aulaandroid.pokedex.model

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    val results: List<PokemonListItem>,
    val sprites: PokemonSprites
)

data class PokemonListItem(
    val name: String,
    val url: String,
    val frontDefault: String,
    val pokemonSprites: PokemonSprites
)

data class Pokemon (
    val id: Int,
    val name: String,
    val types: List<PokemonTypeSlot>,
    val abilities: List<PokemonAbilitySlot>,
    val stats: List<PokemonStatSlot>,
    val weight: Int,
    val height: Int,
    val sprites: PokemonSprites
)

data class PokemonTypeSlot(
    val slot: Int,
    val type: PokemonType
)

data class PokemonType(
    val name: String,
    val url: String
)

data class PokemonAbilitySlot(
    val ability: PokemonAbility,
    @SerializedName("is_hidden") val isHidden: Boolean,
    val slot: Int
)

data class PokemonAbility(
    val name: String,
    val url: String
)

data class PokemonStatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    val effort: Int,
    val stat: PokemonStat
)

data class PokemonStat(
    val name: String,
    val url: String
)

data class PokemonSprites(
    @SerializedName("front_default") val frontDefault: String
)