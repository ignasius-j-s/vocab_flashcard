package io.ign.vocabflashcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.data.DecksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(val deckList: List<Deck> = listOf())


class HomeViewModel(
    private val decksRepository: DecksRepository,
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

    fun saveDeck(newDeck: Deck) {
        if (newDeck.isValid()) {
            viewModelScope.launch {
                val maxOrder = decksRepository.getDeckMaxOrder() ?: 0
                decksRepository.insertDeck(newDeck.copy(order = maxOrder + 1))
            }
        }
    }

    fun editDeck(oldDeck: Deck, newDeck: Deck) {
        if (newDeck.isValid()) {
            viewModelScope.launch {
                decksRepository.updateDeck(newDeck.copy(id = oldDeck.id))
            }
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            decksRepository.deleteDeck(deck)
        }
    }
}
