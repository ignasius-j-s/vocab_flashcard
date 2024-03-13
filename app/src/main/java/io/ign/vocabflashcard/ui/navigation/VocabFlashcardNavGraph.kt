package io.ign.vocabflashcard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.ign.vocabflashcard.ui.home.HomeDestination
import io.ign.vocabflashcard.ui.home.HomeScreen

@Composable
fun VocabFlashcardNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier,
    ) {
        composable(HomeDestination.route) {
            HomeScreen(
                navigateToFlashcardEntry = { /* TODO */ },
                navigateToFlashcardUpdate = { /* TODO */ }
            )
        }
    }
}