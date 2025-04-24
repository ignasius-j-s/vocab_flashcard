package io.ign.vocabflashcard.ui.home

//import androidx.compose.ui.tooling.preview.Preview
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.ui.AppViewModelProvider
import io.ign.vocabflashcard.ui.CustomTextField
import io.ign.vocabflashcard.ui.navigation.NavigationDestination

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToDeck: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var showNewDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedDeck by remember { mutableStateOf(Deck(name = "")) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.deck_add)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.deck_add)
                    )
                },
                onClick = { showNewDialog = true },
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    ) { innerPadding ->
        HomeBody(
            deckList = homeUiState.deckList,
            favFlashcardCount = 0, //TODO
            onDeckClick = navigateToDeck,
            onDelete = { deck ->
                selectedDeck = deck
                showDeleteDialog = true
            },
            onEdit = { deck ->
                selectedDeck = deck
                showEditDialog = true
            },
            modifier = modifier
                .padding(innerPadding)
        )
    }

    if (showNewDialog) {
        NewDeckDialog(
            onDismiss = { showNewDialog = false },
            onOkClick = { deck -> viewModel.saveDeck(deck) }
        )
    }

    if (showEditDialog) {
        EditDeckDialog(
            value = selectedDeck.name,
            onDismiss = { showEditDialog = false },
            onOkClick = { deck -> viewModel.editDeck(selectedDeck, deck) }
        )
    }

    if (showDeleteDialog) {
        DeleteDeckDialog(
            onDismiss = { showDeleteDialog = false },
            onOkClick = { viewModel.deleteDeck(selectedDeck) }
        )
    }
}

@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    deckList: List<Deck>,
    favFlashcardCount: Int = 0,
    onDeckClick: (Int) -> Unit,
    onDelete: (Deck) -> Unit,
    onEdit: (Deck) -> Unit,
) {
    Column(modifier = modifier) {
        FavoriteEntry(
            onClick = {},
            favFlashcardCount = favFlashcardCount,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_extra_small))
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small)))

        if (deckList.isEmpty()) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.deck_empty),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            DeckList(
                deckList = deckList,
                onDeckClick = { onDeckClick(it.id) },
                onDelete = onDelete,
                onEdit = onEdit,
            )
        }
    }
}

@Composable
fun DeckList(
    deckList: List<Deck>,
    onDeckClick: (Deck) -> Unit,
    onDelete: (Deck) -> Unit,
    onEdit: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_extra_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_extra_small))
        ) {
            items(items = deckList, key = { it.id }) { deck ->
                DeckEntry(
                    deck = deck,
                    onDeckClick = onDeckClick,
                    onDelete = onDelete,
                    onEdit = onEdit
                )
            }
        }
    }
}

@Composable
fun FavoriteEntry(
    onClick: () -> Unit,
    favFlashcardCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.star_outline_24),
                contentDescription = stringResource(R.string.favorites),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                tint = Color(0xFFE7FC3F)
            )
            Text(
                text = stringResource(R.string.favorites),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = favFlashcardCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier
                    .alpha(0.8f)
                    .padding(horizontal = dimensionResource(R.dimen.padding_large))
            )
        }
    }
}

@Composable
fun DeckEntry(
    deck: Deck,
    onDeckClick: (Deck) -> Unit,
    onDelete: (Deck) -> Unit,
    onEdit: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onDeckClick(deck) }
    ) {
        var moreMenu by remember { mutableStateOf(false) }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.List,
                contentDescription = "list_${deck.name}",
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
                                    contentDescription = stringResource(R.string.deck_edit)
                                )
                                Spacer(Modifier.padding(horizontal = dimensionResource(R.dimen.padding_extra_small)))
                                Text(
                                    stringResource(R.string.deck_edit),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        onClick = { onEdit(deck); moreMenu = false }
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
fun NewDeckDialog(
    onDismiss: () -> Unit,
    onOkClick: (Deck) -> Unit
) {
    DeckDialog(
        onDismiss = onDismiss,
        onOkClick = { deckName ->
            val deck = Deck(name = deckName)
            onOkClick(deck)
        },
        title = R.string.deck_add,
    )
}

@Composable
fun EditDeckDialog(
    value: String,
    onDismiss: () -> Unit,
    onOkClick: (Deck) -> Unit
) {
    DeckDialog(
        value,
        onDismiss = onDismiss,
        onOkClick = { deckName ->
            val deck = Deck(name = deckName)
            onOkClick(deck)
        },
        title = R.string.deck_edit,
    )
}

@Composable
fun DeleteDeckDialog(
    onDismiss: () -> Unit,
    onOkClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onOkClick()
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.ok_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_btn))
            }
        },
        title = {
            Text(stringResource(R.string.deck_delete))
        },
        text = {
            Text(stringResource(R.string.deck_delete_confirmation))
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = stringResource(R.string.deck_delete),
                tint = Color(0xFFEF5350)
            )
        }
    )
}

@Composable
fun DeckDialog(
    value: String = "",
    onDismiss: () -> Unit,
    onOkClick: (String) -> Unit,
    @StringRes title: Int,
) {
    var enableOkButton by remember { mutableStateOf(false) }
    var deckName by remember { mutableStateOf(value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onOkClick(deckName)
                    onDismiss()
                },
                enabled = enableOkButton
            ) {
                Text(stringResource(R.string.ok_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel_btn))
            }
        },
        title = {
            Text(stringResource(title))
        },
        text = {
            Column {
                CustomTextField(
                    value = deckName,
                    onValueChange = {
                        enableOkButton = it.isNotBlank()
                        deckName = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
