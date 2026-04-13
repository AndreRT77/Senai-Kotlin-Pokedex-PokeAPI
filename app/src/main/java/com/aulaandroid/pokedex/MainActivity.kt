package com.aulaandroid.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aulaandroid.pokedex.screens.PokedexScreen
import com.aulaandroid.pokedex.screens.PokemonScreen
import com.aulaandroid.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "PokedexScreen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(
                            route = "PokedexScreen",
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(750)
                                ) + fadeOut(animationSpec = tween(750))
                            },
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(750)
                                ) + fadeIn(animationSpec = tween(750))
                            }
                        ) {
                            PokedexScreen(navController = navController)
                        }
                        composable(
                            route = "PokemonScreen/{pokemonName}",
                            arguments = listOf(navArgument("pokemonName") { type = NavType.StringType }),
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(750)
                                ) + fadeIn(animationSpec = tween(750))
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(750)
                                ) + fadeOut(animationSpec = tween(750))
                            }
                        ) { backStackEntry ->
                            val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
                            PokemonScreen(navController = navController, pokemonName = pokemonName)
                        }
                    }
                }
            }
        }
    }
}
