package io.ign.vocabflashcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Card
import io.ign.vocabflashcard.data.CardsRepository
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.data.DecksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(val deckList: List<Deck> = listOf())

sealed class CardViewKind {
    object None : CardViewKind()
    class Show(val card: Card) : CardViewKind()
    class Edit(val card: Card) : CardViewKind()
    class Create(val deckId: Int) : CardViewKind()
}

class HomeViewModel(
    private val decksRepository: DecksRepository,
    private val cardsRepository: CardsRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val homeUiState: StateFlow<HomeUiState> = decksRepository.getAllDecksStream()
        .filterNotNull()
        .map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState(emptyList())
        )

    private val _cardViewState = MutableStateFlow(CardViewKind.None as CardViewKind)
    val cardViewState: StateFlow<CardViewKind> = _cardViewState

    fun insertDeck(deck: Deck) {
        if (deck.isValid()) {
            viewModelScope.launch {
                val maxOrder = decksRepository.getMaxOrder() ?: 0
                decksRepository.insertDeck(deck.copy(order = maxOrder + 1))
//                decksRepository.insertDeck(deck)
            }
        }
    }

    fun updateDeck(deck: Deck) {
        if (deck.isValid()) {
            viewModelScope.launch {
                decksRepository.updateDeck(deck)
            }
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            decksRepository.deleteDeck(deck)
        }
    }

    fun swapDeckOrder(deck1: Deck, deck2: Deck) {
        viewModelScope.launch {
            decksRepository.swapDeckOrder(deck1, deck2)
        }
    }

    fun getCards(deckId: Int, searchQuery: String? = null): Flow<List<Card>> {
        val searchQuery = searchQuery ?: ""

        return if (searchQuery.isBlank()) {
            cardsRepository.getAllCardsInDeckStream(deckId)
        } else {
            cardsRepository.getAllCardsInDeckStream(deckId, "%$searchQuery%")
        }
    }

    fun showCard(card: Card) {
        _cardViewState.value = CardViewKind.Show(card)
    }

    fun editCard(card: Card) {
        _cardViewState.value = CardViewKind.Edit(card)
    }

    fun newCard(deckId: Int) {
        _cardViewState.value = CardViewKind.Create(deckId)
    }
}
