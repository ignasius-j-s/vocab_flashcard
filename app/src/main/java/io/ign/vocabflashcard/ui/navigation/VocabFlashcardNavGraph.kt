package io.ign.vocabflashcard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.ign.vocabflashcard.ui.group.GroupScreen
import io.ign.vocabflashcard.ui.group.GroupScreenDestination
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
                navigateToGroup = { id ->
                    navController.navigate(GroupScreenDestination.route + "/$id")
                }
            )
        }
        composable(
            GroupScreenDestination.routeWithArg,
            arguments = listOf(
                navArgument(
                    GroupScreenDestination.idArg,
                    builder = { type = NavType.IntType }
                )
            )
        ) {
            GroupScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}