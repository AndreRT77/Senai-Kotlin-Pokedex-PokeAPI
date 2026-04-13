package com.aulaandroid.pokedex.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.aulaandroid.pokedex.R
import com.aulaandroid.pokedex.model.Pokemon
import com.aulaandroid.pokedex.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

@Composable
fun PokemonScreen(navController: NavController, pokemonName: String) {
    var pokemon by remember { mutableStateOf<Pokemon?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(pokemonName) {
        RetrofitFactory().getPokemonService().getPokemon(pokemonName).enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                if (response.isSuccessful) {
                    pokemon = response.body()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                Log.e("PokemonScreen", "Error fetching pokemon details", t)
                isLoading = false
            }
        })
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFCE4646))
        }
    } else if (pokemon != null) {
        val p = pokemon!!
        val typeColor = getPokemonColor(p.types.firstOrNull()?.type?.name)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(typeColor)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = p.name.replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = String.format(Locale.getDefault(), "#%03d", p.id),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pokeball_),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp),
                    tint = Color.White.copy(alpha = 0.1f)
                )

                Column(
                    modifier = Modifier
                        .padding(top = 140.dp, bottom = 16.dp, start = 4.dp, end = 4.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(top = 56.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        p.types.forEach { typeSlot ->
                            val color = getPokemonColor(typeSlot.type.name)
                            Text(
                                text = typeSlot.type.name.replaceFirstChar { it.uppercase() },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "About",
                        color = typeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoItem(
                            label = "Weight",
                            value = "${p.weight / 10.0} kg",
                            icon = R.drawable.pokeball_
                        )
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.LightGray))
                        InfoItem(
                            label = "Height",
                            value = "${p.height / 10.0} m",
                            icon = R.drawable.pokeball_
                        )
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.LightGray))
                        InfoItem(
                            label = "Moves",
                            value = p.abilities.take(2).joinToString("\n") { it.ability.name.replaceFirstChar { char -> char.uppercase() } },
                            icon = null
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Base Stats",
                        color = typeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    p.stats.forEach { statSlot ->
                        StatRow(statSlot.stat.name, statSlot.baseStat, typeColor)
                    }
                }

                AsyncImage(
                    model = p.sprites.frontDefault,
                    contentDescription = p.name,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.TopCenter)
                        .padding(top = 0.dp)
                )
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String, icon: Int?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = value, fontSize = 14.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun StatRow(label: String, value: Int, color: Color) {
    val statName = when(label) {
        "hp" -> "HP"
        "attack" -> "ATK"
        "defense" -> "DEF"
        "special-attack" -> "SATK"
        "special-defense" -> "SDEF"
        "speed" -> "SPD"
        else -> label.uppercase()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statName,
            modifier = Modifier.width(50.dp),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color.LightGray).padding(horizontal = 8.dp))
        Text(
            text = String.format(Locale.getDefault(), "%03d", value),
            modifier = Modifier.width(40.dp).padding(horizontal = 8.dp),
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        LinearProgressIndicator(
            progress = { value / 255f },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            strokeCap = StrokeCap.Round
        )
    }
}

fun getPokemonColor(type: String?): Color {
    return when (type) {
        "grass" -> Color(0xFF74CB48)
        "fire" -> Color(0xFFF57D31)
        "water" -> Color(0xFF6493EB)
        "bug" -> Color(0xFFA7B723)
        "normal" -> Color(0xFFAAA67F)
        "poison" -> Color(0xFFA43E9E)
        "electric" -> Color(0xFFF9CF30)
        "ground" -> Color(0xFFDEC16B)
        "fairy" -> Color(0xFFE69EAC)
        "fighting" -> Color(0xFFC12239)
        "psychic" -> Color(0xFFFB5584)
        "rock" -> Color(0xFFB69E31)
        "ghost" -> Color(0xFF70559B)
        "ice" -> Color(0xFF9AD6DF)
        "dragon" -> Color(0xFF7037FF)
        "dark" -> Color(0xFF75574C)
        "steel" -> Color(0xFFB7B9D0)
        "flying" -> Color(0xFFA891EC)
        else -> Color.LightGray
    }
}
