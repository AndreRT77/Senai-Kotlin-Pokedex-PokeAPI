package com.aulaandroid.pokedex.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aulaandroid.pokedex.R
import com.aulaandroid.pokedex.model.Pokemon
import com.aulaandroid.pokedex.model.PokemonListItem
import com.aulaandroid.pokedex.model.PokemonResponse
import com.aulaandroid.pokedex.service.RetrofitFactory
import com.aulaandroid.pokedex.ui.theme.PokemonType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

@Composable
fun PokedexScreen(navController: NavController, modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    val pokeList = remember { mutableStateListOf<PokemonListItem>() }
    val pokemonDetails = remember { mutableStateMapOf<String, Pokemon>() }

    LaunchedEffect(Unit) {
        RetrofitFactory().getPokemonService().listPokemon(limit = 50).enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { results ->
                        pokeList.clear()
                        pokeList.addAll(results)
                        
                        results.forEach { item ->
                            RetrofitFactory().getPokemonService().getPokemon(item.name).enqueue(object : Callback<Pokemon> {
                                override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                                    if (response.isSuccessful) {
                                        response.body()?.let { pokemon ->
                                            pokemonDetails[item.name] = pokemon
                                        }
                                    }
                                }
                                override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                                    Log.e("Pokedex", "Error fetching details for ${item.name}", t)
                                }
                            })
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                Log.e("Pokedex", "Error fetching pokemon", t)
            }
        })
    }

    Column(
        modifier = modifier
            .background(color = Color(0xFFCE4646))
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.pokeball_),
                contentDescription = "ícone da pokebola",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
            Text(
                text = "Pokedex",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Nome ou ID") },
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "ícone de busca"
                        )
                    }
                }
            )
            
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filteredList = if (searchText.isEmpty()) {
                    pokeList
                } else {
                    pokeList.filter { it.name.contains(searchText, ignoreCase = true) }
                }

                items(filteredList) { item ->
                    val pokemon = pokemonDetails[item.name]
                    PokemonCard(item, pokemon, navController)
                }
            }
        }
    }
}

@Composable
fun PokemonCard(item: PokemonListItem, pokemon: Pokemon?, navController: NavController) {
    val backgroundColor = PokemonType.fromApiType(
        pokemon?.types?.firstOrNull()?.type?.name
    )

    Card(
        onClick = { navController.navigate("PokemonScreen/${item.name}") },
        modifier = Modifier
            .size(200.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format(Locale.getDefault(), "#%03d", pokemon?.id ?: 0),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(end = 5.dp)
            )
            if (pokemon != null) {
                AsyncImage(
                    model = pokemon.sprites.other?.officialArtwork?.frontDefault ?: pokemon.sprites.frontDefault,
                    contentDescription = pokemon.name,
                    modifier = Modifier.size(100.dp)
                )
            }


            Text(
                text = item.name.replaceFirstChar { it.uppercase() },
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
