package io.ign.vocabflashcard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.ign.vocabflashcard.ui.group.FlashcardScreen
import io.ign.vocabflashcard.ui.group.FlashcardScreenDestination
import io.ign.vocabflashcard.ui.home.HomeScreenDestination
import io.ign.vocabflashcard.ui.home.HomeScreen

@Composable
fun VocabFlashcardNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreenDestination.route,
        modifier = modifier,
    ) {
        composable(HomeScreenDestination.route) {
            HomeScreen(
                navigateToGroupEntry = { id ->
                    navController.navigate(FlashcardScreenDestination.route + "/$id")
                }
            )
        }
        composable(
            FlashcardScreenDestination.routeWithArg,
            arguments = listOf(
                navArgument(
                    FlashcardScreenDestination.idArg,
                    builder = { type = NavType.IntType }
                )
            )
        ) {
            FlashcardScreen()
        }
    }
}