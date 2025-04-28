package io.ign.vocabflashcard.ui.home

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.data.Deck
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
    class Menu(val selectedDeck: Deck, val deckIndex: Int) : DialogKind()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToDeck: (Deck) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
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
                shape = RoundedCornerShape(dimensionResource(R.dimen.round)),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    ) { innerPadding ->
        HomeContent(
            deckList = homeUiState.deckList,
            onDeckClick = navigateToDeck,
            onDeckLongClick = { deck, index -> showDialog = DialogKind.Menu(deck, index) },
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }

    val onMoveUp = { deck: Deck, index: Int ->
        if (index > 0) {
            val previousDeck = homeUiState.deckList[index - 1]
            viewModel.swapDeckOrder(deck, previousDeck)
        }
    }

    val onMoveDown = { deck: Deck, index: Int ->
        if (index < homeUiState.deckList.lastIndex) {
            val nextDeck = homeUiState.deckList[index + 1]
            viewModel.swapDeckOrder(deck, nextDeck)
        }
    }

    when (showDialog) {
        is DialogKind.None -> {}
        else -> {
            DeckDialog(
                viewModel,
                showDialog,
                onDismiss = { showDialog = DialogKind.None },
                onMoveUp,
                onMoveDown,
                onRename = { deck -> showDialog = DialogKind.Rename(deck) },
                onDelete = { deck -> showDialog = DialogKind.Delete(deck) },
            )
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    deckList: List<Deck>,
    onDeckClick: (Deck) -> Unit,
    onDeckLongClick: (Deck, Int) -> Unit,
    viewModel: HomeViewModel,
) {
    if (deckList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.deck_empty),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            itemsIndexed(deckList, key = { i, deck -> deck.id }) { index, deck ->
                DeckItem(
                    index,
                    deck,
                    onDeckClick,
                    onDeckLongClick,
                    viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeckItem(
    index: Int,
    deck: Deck,
    onClick: (Deck) -> Unit,
    onLongClick: (Deck, Int) -> Unit,
    viewModel: HomeViewModel,
) {
    var searchQuery by remember { mutableStateOf("") }
    val cardList = viewModel.getCards(deck.id, searchQuery).collectAsState(emptyList())

    Box(
        Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(dimensionResource(R.dimen.round))
            )
            .combinedClickable(onLongClick = { onLongClick(deck, index) }, onClick = {})
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_small))
            ) {
                Text(
                    deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.updateDeck(deck.copy(expanded = !deck.expanded)) }) {
                    if (deck.expanded) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "hide"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowUp,
                            contentDescription = "show"
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = deck.expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_small))
                ) {
                    val height = 50.dp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_small))
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.labelMedium,
                            placeholder = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Search, null)
                                    Text(
                                        stringResource(R.string.search_card_placeholder),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(height)
                        )
                        Button(
                            onClick = {},
                            shape = RoundedCornerShape(dimensionResource(R.dimen.round)),
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                            modifier = Modifier.height(height)
                        ) {
                            Icon(Icons.Outlined.Add, null)
                            Text(
                                stringResource(R.string.card_new),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
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
    onMoveUp: (Deck, Int) -> Unit,
    onMoveDown: (Deck, Int) -> Unit,
    onRename: (Deck) -> Unit,
    onDelete: (Deck) -> Unit,
) {
    var deckName by remember { mutableStateOf("") }
    var enableOkButton by remember { mutableStateOf(false) }
    var onOkClick: () -> Unit? = {}
    var title: @Composable (() -> Unit)? = null
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
            title = { Text(stringResource(R.string.deck_create)) }
            onOkClick = { viewModel.insertDeck(Deck(name = deckName)) }
        }

        is DialogKind.Rename -> {
            title = { Text(stringResource(R.string.deck_rename)) }
            onOkClick = { viewModel.updateDeck(dialogKind.renamedDeck.copy(name = deckName)) }
        }

        is DialogKind.Delete -> {
            title = { Text(stringResource(R.string.deck_delete)) }
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

        is DialogKind.Menu -> {
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMoveUp(dialogKind.selectedDeck, dialogKind.deckIndex)
                                onDismiss()
                            }
                            .padding(dimensionResource(R.dimen.padding_medium))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.move_up), modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.KeyboardArrowUp, null)
                        }
                    }
                    HorizontalDivider()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMoveDown(dialogKind.selectedDeck, dialogKind.deckIndex)
                                onDismiss()
                            }
                            .padding(dimensionResource(R.dimen.padding_medium))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.move_down), modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.KeyboardArrowDown, null)
                        }
                    }
                    HorizontalDivider()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRename(dialogKind.selectedDeck) }
                            .padding(dimensionResource(R.dimen.padding_medium))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.rename), modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
                    HorizontalDivider()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDelete(dialogKind.selectedDeck) }
                            .padding(dimensionResource(R.dimen.padding_medium))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.delete), modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.Delete, null)
                        }
                    }
                }
            }
        }

        else -> {
            onDismiss()
        }
    }

    AlertDialog(
        shape = RoundedCornerShape(dimensionResource(R.dimen.round)),

        onDismissRequest = onDismiss,
        confirmButton = {
            if (dialogKind !is DialogKind.Menu) {
                TextButton(
                    onClick = { onOkClick(); onDismiss() },
                    contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.round)),
                    enabled = enableOkButton
                ) {
                    Text(stringResource(R.string.ok_btn))
                }
            }
        },
        dismissButton = {
            if (dialogKind !is DialogKind.Menu) {
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.round))
                ) {
                    Text(stringResource(R.string.cancel_btn))
                }
            }
        },
        title = title,
        icon = icon,
        text = text,
    )
}
