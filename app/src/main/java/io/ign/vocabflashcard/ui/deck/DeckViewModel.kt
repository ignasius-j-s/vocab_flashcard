package io.ign.vocabflashcard.ui.deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Card
import io.ign.vocabflashcard.data.DeckWithCards
import io.ign.vocabflashcard.data.DecksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DeckUiState(
    val id: Int = 0,
    val name: String = "",
    val cards: List<Card> = listOf()
)

fun DeckWithCards.toDeckUiState(): DeckUiState {
    return DeckUiState(this.deck.id, this.deck.name, this.cards)
}

class DeckViewModel(
    savedStateHandle: SavedStateHandle,
    decksRepository: DecksRepository
) : ViewModel() {
    private val deckId: Int = checkNotNull(savedStateHandle[DeckScreenDestination.ARG_ID])

    val deckUiState: StateFlow<DeckUiState> =
        decksRepository.getDeckWithCardsStream(deckId)
            .filterNotNull()
            .map { it.toDeckUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeckUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
