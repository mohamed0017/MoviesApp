package com.personal.moviesapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.personal.moviesapp.data.model.MovieResponseItem
import com.personal.moviesapp.presentation.details.MovieDetailsScreen
import com.personal.moviesapp.presentation.movies.MovieListScreen
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    encodeDefaults = true
}

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MovieList.route
    ) {
        composable(Screen.MovieList.route) {
            MovieListScreen(
                onMovieClick = { movie ->
                    try {
                        val route = Screen.MovieDetails.createRoute(movie)
                        navController.navigate(route)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            )
        }

        composable(
            route = Screen.MovieDetails.route,
            arguments = listOf(
                navArgument("movieJson") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val movieJson = backStackEntry.arguments?.getString("movieJson")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            } ?: ""

            val movie = try {
                json.decodeFromString(MovieResponseItem.serializer(), movieJson)
            } catch (e: Exception) {
                e.printStackTrace()
                MovieResponseItem()
            }

            MovieDetailsScreen(
                movie = movie,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
} 