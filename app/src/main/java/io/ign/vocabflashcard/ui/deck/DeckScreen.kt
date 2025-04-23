package io.ign.vocabflashcard.ui.deck

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.ui.AppViewModelProvider
import io.ign.vocabflashcard.ui.TopBar
import io.ign.vocabflashcard.ui.navigation.NavigationDestination

object DeckScreenDestination : NavigationDestination {
    override val route = "deck"

    const val ARG_ID = "id"
    val routeWithArg = "$route/{$ARG_ID}"
}

@Composable
fun DeckScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val deckUiState by viewModel.deckUiState.collectAsState()
//    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = true,
                title = deckUiState.name,
                showSettingButton = false,
                onNavBackClick = navigateBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.card_add)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.card_add)
                    )
                },
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    ) {
        Box(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (deckUiState.cards.isEmpty()) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.card_empty),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}
