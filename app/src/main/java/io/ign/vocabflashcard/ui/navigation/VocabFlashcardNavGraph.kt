package io.ign.vocabflashcard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.ui.deck.DeckScreen
import io.ign.vocabflashcard.ui.deck.DeckScreenDestination
import io.ign.vocabflashcard.ui.home.HomeScreen
import io.ign.vocabflashcard.ui.home.HomeScreenDestination

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
                navigateToDeck = { deck: Deck ->
                    navController.navigate(DeckScreenDestination.route + "/${deck.id}")
                }
            )
        }
        composable(
            DeckScreenDestination.routeWithArg,
            arguments = listOf(
                navArgument(
                    DeckScreenDestination.ARG_ID,
                    builder = { type = NavType.IntType }
                )
            )
        ) {
            DeckScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}
