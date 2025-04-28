package io.ign.vocabflashcard.ui.deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Card
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.data.DecksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DeckUiState(
    val deckId: Int = 0,
    val deckName: String = "",
    val cardList: List<Card> = listOf()
)

fun Deck.toDeckUiState(): DeckUiState {
    return DeckUiState(this.id, this.name, emptyList())
}

class DeckViewModel(
    savedStateHandle: SavedStateHandle,
    decksRepository: DecksRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val deckId: Int = checkNotNull(savedStateHandle[DeckScreenDestination.ARG_ID])

    val deckUiState: StateFlow<DeckUiState> = decksRepository.getDeckStream(deckId)
        .filterNotNull()
        .map { it.toDeckUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = DeckUiState()
        )
}
