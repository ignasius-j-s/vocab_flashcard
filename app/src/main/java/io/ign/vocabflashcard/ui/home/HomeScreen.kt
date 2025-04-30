package io.ign.vocabflashcard.ui.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.ign.vocabflashcard.data.Card
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.ui.AppViewModelProvider
import io.ign.vocabflashcard.ui.CustomTextField
import io.ign.vocabflashcard.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToDeck: (Deck) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val deckDialogState by viewModel.deckDialogState.collectAsState()
    val cardViewState by viewModel.cardViewState.collectAsState()

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
                onClick = { viewModel.createDeckDialog() },
                shape = RoundedCornerShape(dimensionResource(R.dimen.round)),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    ) { innerPadding ->
        HomeContent(
            deckList = homeUiState.deckList,
            onDeckClick = navigateToDeck,
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

    when (deckDialogState) {
        is DialogKind.None -> {}
        else -> {
            DeckDialog(
                viewModel,
                deckDialogState,
                onDismiss = { viewModel.hideDeckDialog() },
                onMoveUp,
                onMoveDown,
            )
        }
    }

    when (cardViewState) {
        is CardViewKind.Create -> {
            val card = Card(
                term = "",
                description = "",
                note = "",
                deckId = (cardViewState as CardViewKind.Create).deckId
            )
            CardModalBottomSheet(
                card,
                onDismiss = { viewModel.hideCardView() },
                onOkClick = { card -> viewModel.insertCard(card) }
            )
        }

        else -> {
            viewModel.hideCardView()
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    deckList: List<Deck>,
    onDeckClick: (Deck) -> Unit,
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
    viewModel: HomeViewModel,
) {
    var searchQuery by remember { mutableStateOf("") }
    val cardList by viewModel.getCards(deck.id, searchQuery).collectAsState(emptyList())

    Box(
        Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(dimensionResource(R.dimen.round))
            )
            .combinedClickable(
                onClick = { onClick(deck) },
                onLongClick = { viewModel.menuDeckDialog(deck, index) },
            )
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${cardList.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
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
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    val height = 50.dp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
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
                            onClick = { viewModel.newCard(deck.id) },
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
                    val modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)

                    if (cardList.isEmpty()) {
                        Box(modifier = modifier, contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.card_empty),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    } else {
                        val pagerState = rememberPagerState(pageCount = { cardList.size })
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = dimensionResource(R.dimen.padding_small)
                        ) { currentPage ->
                            Box(modifier = modifier) {
                                CardItem(cardList[currentPage])
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(
    card: Card,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(dimensionResource(R.dimen.round))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
        ) {
            Text(card.term, style = MaterialTheme.typography.labelLarge)
            Text(card.description, style = MaterialTheme.typography.labelMedium)
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
                            .clickable { viewModel.renameDeckDialog(dialogKind.selectedDeck) }
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
                            .clickable { viewModel.deleteDeckDialog(dialogKind.selectedDeck) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardModalBottomSheet(
    card: Card,
    onOkClick: (Card) -> Unit,
    onDismiss: () -> Unit,
) {
    var cardTerm by remember { mutableStateOf(card.term) }
    var cardDescription by remember { mutableStateOf(card.description) }
    var cardNote by remember { mutableStateOf(card.note) }
    val enableOkButton = cardTerm.isNotBlank() && cardDescription.isNotBlank()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_extra_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onDismiss()
                        }
                    }
                }) { Text("Cancel") }
                Button(enabled = enableOkButton, onClick = {
                    val card =
                        card.copy(term = cardTerm, description = cardDescription, note = cardNote)
                    onOkClick(card); onDismiss()
                }) { Text("Save") }
            }
            OutlinedTextField(
                cardTerm,
                onValueChange = { cardTerm = it },
                singleLine = true,
                label = { Text("Term") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                cardDescription,
                onValueChange = { cardDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                cardNote,
                onValueChange = { cardNote = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
