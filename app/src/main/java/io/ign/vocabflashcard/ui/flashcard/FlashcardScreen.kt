package io.ign.vocabflashcard.ui.flashcard

import androidx.compose.runtime.Composable
import io.ign.vocabflashcard.ui.navigation.NavigationDestination

object FlashcardScreenDestination : NavigationDestination {
    override val route = "flashcard"

    val idArg = "id"
    val routeWithArg = "$route/{$idArg}"
}

@Composable
fun FlashcardScreen() {
}