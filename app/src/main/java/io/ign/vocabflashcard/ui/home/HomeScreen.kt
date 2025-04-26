package io.ign.vocabflashcard.ui.home

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.data.DeckData
import io.ign.vocabflashcard.ui.AppViewModelProvider
import io.ign.vocabflashcard.ui.CustomTextField
import io.ign.vocabflashcard.ui.navigation.NavigationDestination

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
}

sealed class DialogKind {
    object None : DialogKind()
    object Create : DialogKind()
    class Rename(val renamedDeck: Deck) : DialogKind()
    class Delete(val deletedDeck: Deck) : DialogKind()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToDeck: (Deck) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var showDialog by remember { mutableStateOf(DialogKind.None as DialogKind) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.deck_new)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.deck_new)
                    )
                },
                onClick = { showDialog = DialogKind.Create },
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    ) { innerPadding ->
        HomeContent(
            deckDataList = homeUiState.deckDataList,
            onDeckClick = navigateToDeck,
            onDeckDelete = { deck -> showDialog = DialogKind.Delete(deck) },
            onDeckRename = { deck -> showDialog = DialogKind.Rename(deck) },
            modifier = Modifier.padding(innerPadding)
        )
    }

    when (showDialog) {
        is DialogKind.None -> {}
        else -> DeckDialog(viewModel, showDialog, onDismiss = { showDialog = DialogKind.None })
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    deckDataList: List<DeckData>,
    onDeckClick: (Deck) -> Unit,
    onDeckDelete: (Deck) -> Unit,
    onDeckRename: (Deck) -> Unit,
) {
    if (deckDataList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.deck_empty),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else {
        LazyColumn(
            modifier,
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_extra_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_extra_small))
        ) {
            itemsIndexed(deckDataList, key = { i, data -> data.deck.id }) { i, data ->
                DeckEntry(data.deck, onDeckClick, onDeckDelete, onDeckRename)
            }
        }
    }
}

@Composable
fun DeckEntry(
    deck: Deck,
    onClick: (Deck) -> Unit,
    onDelete: (Deck) -> Unit,
    onRename: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(deck) }
    ) {
        var moreMenu by remember { mutableStateOf(false) }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.List,
                contentDescription = deck.name,
                Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            )
            Text(
                text = deck.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box {
                IconButton(onClick = { moreMenu = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "more"
                    )
                }
                DropdownMenu(expanded = moreMenu, onDismissRequest = { moreMenu = false }) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = stringResource(R.string.deck_rename)
                                )
                                Spacer(Modifier.padding(horizontal = dimensionResource(R.dimen.padding_extra_small)))
                                Text(
                                    stringResource(R.string.deck_rename),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        onClick = { onRename(deck); moreMenu = false }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = stringResource(R.string.deck_delete),
                                    tint = Color(0xFFEF5350)
                                )
                                Spacer(Modifier.padding(horizontal = dimensionResource(R.dimen.padding_extra_small)))
                                Text(
                                    stringResource(R.string.deck_delete),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        onClick = { onDelete(deck); moreMenu = false }
                    )
                }
            }
        }
    }
}

@Composable
fun DeckDialog(
    viewModel: HomeViewModel,
    dialogKind: DialogKind,
    onDismiss: () -> Unit,
) {
    var deckName by remember { mutableStateOf("") }
    var enableOkButton by remember { mutableStateOf(false) }
    var onOkClick: () -> Unit = {}
    var title = ""
    var icon: @Composable (() -> Unit)? = null
    var text: @Composable (() -> Unit)? = {
        CustomTextField(
            value = deckName,
            onValueChange = {
                enableOkButton = it.isNotBlank()
                deckName = it
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    when (dialogKind) {
        is DialogKind.Create -> {
            title = stringResource(R.string.deck_create)
            onOkClick = { viewModel.saveDeck(Deck(name = deckName)) }
        }

        is DialogKind.Rename -> {
            title = stringResource(R.string.deck_rename)
            onOkClick = { viewModel.editDeck(dialogKind.renamedDeck, Deck(name = deckName)) }
        }

        is DialogKind.Delete -> {
            title = stringResource(R.string.deck_delete)
            onOkClick = { viewModel.deleteDeck(dialogKind.deletedDeck) }
            enableOkButton = true
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = stringResource(R.string.deck_delete),
                    tint = Color(0xFFEF5350)
                )
            }
            text = {
                Text(stringResource(R.string.deck_delete_confirmation))
            }
        }

        else -> {}
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onOkClick(); onDismiss() },
                enabled = enableOkButton
            ) {
                Text(stringResource(R.string.ok_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_btn))
            }
        },
        title = { Text(title) },
        icon = icon,
        text = text
    )
}
