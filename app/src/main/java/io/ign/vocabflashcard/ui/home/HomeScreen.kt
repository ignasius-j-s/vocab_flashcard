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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.data.Card
import io.ign.vocabflashcard.data.CardData
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.ui.AppViewModelProvider
import io.ign.vocabflashcard.ui.CardModalBottomSheet
import io.ign.vocabflashcard.ui.UnderlinedTextField
import io.ign.vocabflashcard.ui.navigation.NavigationDestination
import kotlinx.coroutines.flow.filterNotNull

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToDeck: (Deck) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val deckListState by viewModel.deckListState.collectAsState()
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
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    ) { innerPadding ->
        HomeContent(
            deckList = deckListState,
            onDeckClick = navigateToDeck,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }

    val onMoveUp = { deck: Deck, index: Int ->
        if (index > 0) {
            val previousDeck = deckListState[index - 1]
            viewModel.swapDeckOrder(deck, previousDeck)
        }
    }

    val onMoveDown = { deck: Deck, index: Int ->
        if (index < deckListState.lastIndex) {
            val nextDeck = deckListState[index + 1]
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
            val cardData = CardData(card)
            CardModalBottomSheet(
                cardData,
                onDismiss = { viewModel.hideCardView() },
                onOkClick = { cardData, tl, usg -> viewModel.saveCardData(cardData) }
            )
        }

        is CardViewKind.Edit -> {
            val card = (cardViewState as CardViewKind.Edit).card
            val cardData by viewModel.fetchCardDataStream(card.id)
                .filterNotNull()
                .collectAsState(initial = CardData(card))

            CardModalBottomSheet(
                cardData,
                onDismiss = { viewModel.hideCardView() },
                onOkClick = { cardData, translationDeleteList, usageDeleteList ->
                    viewModel.saveCardData(cardData)
                    viewModel.deleteCardTranslations(translationDeleteList)
                    viewModel.deleteCardUsages(usageDeleteList)
                }
            )
        }

        is CardViewKind.Show -> CardDetailsDialog(
            (cardViewState as CardViewKind.Show).card,
            viewModel
        )

        else -> viewModel.hideCardView()
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
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            contentPadding = PaddingValues(
                top = dimensionResource(R.dimen.padding_medium),
                bottom = 120.dp, // make the last deck is not blocked by fab
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium)
            )
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
    val cardList by viewModel.fetchCardsStream(deck.id, searchQuery)
        .collectAsState(initial = emptyList())
    val roundCornerShape = RoundedCornerShape(dimensionResource(R.dimen.round_radius))
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = roundCornerShape
            )
            .clip(roundCornerShape)
            .combinedClickable(
                onClick = { onClick(deck) },
                onLongClick = { viewModel.menuDeckDialog(deck, index) },
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .size(dimensionResource(R.dimen.card_count_size))
                ) {
                    Text(
                        "${cardList.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Text(
                    deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { viewModel.updateDeck(deck.copy(expanded = !deck.expanded)) },
                    modifier = Modifier.size(dimensionResource(R.dimen.card_count_size)),
                ) {
                    val imageVector = if (deck.expanded) {
                        Icons.Outlined.KeyboardArrowDown
                    } else {
                        Icons.Outlined.KeyboardArrowUp
                    }
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            AnimatedVisibility(
                visible = deck.expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(R.dimen.padding_small)),
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
                                .height(dimensionResource(R.dimen.card_search_text_field_height))
                        )
                        Button(
                            onClick = { viewModel.newCard(deck.id) },
                            shape = RoundedCornerShape(dimensionResource(R.dimen.round_radius)),
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                            modifier = Modifier.height(dimensionResource(R.dimen.card_search_text_field_height))
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
                        .height(dimensionResource(R.dimen.card_height))

                    if (cardList.isEmpty()) {
                        Box(modifier = modifier, contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.card_empty),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    } else {
                        val pagerState = rememberPagerState(pageCount = { cardList.size })
                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                            pageSpacing = dimensionResource(R.dimen.padding_small)
                        ) { currentPage ->
                            Box(modifier = modifier) {
                                CardItem(cardList[currentPage], viewModel)
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
    viewModel: HomeViewModel,
) {
    val roundCornerShape = RoundedCornerShape(dimensionResource(R.dimen.round_radius))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = roundCornerShape
            )
            .clip(roundCornerShape)
            .clickable { viewModel.showCard(card) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
        ) {
            Text(
                card.term,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                card.description,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondary,
            )
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
        UnderlinedTextField(
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
            title = { Text(stringResource(R.string.deck_new)) }
            onOkClick = { viewModel.insertDeck(Deck(name = deckName)) }
        }

        is DialogKind.Rename -> {
            title = { Text(stringResource(R.string.deck_rename)) }
            onOkClick = { viewModel.updateDeck(dialogKind.renamedDeck.copy(name = deckName)) }
        }

        is DialogKind.Remove -> {
            title = { Text(stringResource(R.string.deck_delete)) }
            onOkClick = { viewModel.deleteDeck(dialogKind.deletedDeck) }
            enableOkButton = true
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = stringResource(R.string.deck_delete),
                    tint = MaterialTheme.colorScheme.error
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
        shape = RoundedCornerShape(dimensionResource(R.dimen.round_radius)),
        onDismissRequest = onDismiss,
        confirmButton = {
            if (dialogKind !is DialogKind.Menu) {
                TextButton(
                    onClick = { onOkClick(); onDismiss() },
                    contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.round_radius)),
                    enabled = enableOkButton
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        },
        dismissButton = {
            if (dialogKind !is DialogKind.Menu) {
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.round_radius))
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        },
        title = title,
        icon = icon,
        text = text,
    )
}

@Composable
fun CardDetailsDialog(card: Card, viewModel: HomeViewModel) {
    val onDismiss = { viewModel.hideCardView() }
    val cardDataState by viewModel.fetchCardDataStream(card.id).filterNotNull()
        .collectAsState(initial = CardData(card))

    Dialog(onDismissRequest = onDismiss) {
        /* TODO */
        Column {
            Text(cardDataState.card.term)
            Text(cardDataState.card.description)
            Text(cardDataState.card.note)
        }
    }
}
