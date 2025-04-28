package io.ign.vocabflashcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Card
import io.ign.vocabflashcard.data.CardsRepository
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.data.DecksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(val deckList: List<Deck> = listOf())

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

    fun insertDeck(deck: Deck) {
        if (deck.isValid()) {
//            TODO: investigate why this caused crash
//            val maxOrder = decksRepository.getDeckMaxOrder()
            viewModelScope.launch {
//                decksRepository.insertDeck(newDeck.copy(order = maxOrder + 1))
                decksRepository.insertDeck(deck)
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

    fun getCards(deckId: Int, searchQuery: String? = null): Flow<List<Card>> {
        val searchQuery = searchQuery ?: ""
        return if (searchQuery.isBlank()) {
            cardsRepository.getAllCardsInDeckStream(deckId)
        } else {
            cardsRepository.getAllCardsInDeckStream(deckId, "%$searchQuery%")
        }
    }
}
