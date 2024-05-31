package io.ign.vocabflashcard.ui.group

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
import androidx.compose.runtime.rememberCoroutineScope
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

object GroupScreenDestination : NavigationDestination {
    override val route = "group"

    val idArg = "id"
    val routeWithArg = "$route/{$idArg}"
}

@Composable
fun GroupScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val groupUiState by viewModel.groupUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = true,
                title = groupUiState.name,
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
            if (groupUiState.cardList.isEmpty()) {
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