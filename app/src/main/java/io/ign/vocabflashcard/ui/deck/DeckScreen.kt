package io.ign.vocabflashcard.ui.deck

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.ui.AppViewModelProvider
import io.ign.vocabflashcard.ui.navigation.NavigationDestination

object DeckScreenDestination : NavigationDestination {
    override val route = "deck"
    const val ARG_ID = "id"
    val routeWithArg = "$route/{$ARG_ID}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScreen(
    navigateBack: () -> Unit,
    viewModel: DeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val deckUiState by viewModel.deckUiState.collectAsState()
}
