package io.ign.vocabflashcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.data.DecksRepository
import io.ign.vocabflashcard.data.UserPreferencesRepository
import io.ign.vocabflashcard.data.UserPrefs
import io.ign.vocabflashcard.ui.setting.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(val deckList: List<Deck> = listOf())

class HomeViewModel(
    private val decksRepository: DecksRepository,
    userPrefsRepository: UserPreferencesRepository
) : ViewModel() {
    private val userPrefs: StateFlow<UserPrefs> =
        userPrefsRepository.getUserPrefs().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = UserPrefs("NAME", false)
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val deckList = userPrefs.flatMapLatest {
        decksRepository.getAllDecksStream(SortOrder.valueOf(it.sortOrder), it.isDescending)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    private val _homeUiState = MutableStateFlow(HomeUiState())

    val homeUiState: StateFlow<HomeUiState> =
        combine(_homeUiState, deckList, userPrefs) { homeUiState, deckList, _ ->
            homeUiState.copy(deckList = deckList)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    suspend fun saveDeck(deck: Deck) {
        if (deck.name.isNotBlank()) {
            val newDeck = deck.copy(createdAt = System.currentTimeMillis())
            decksRepository.insertDeck(newDeck)
        }
    }

    suspend fun editDeck(deck: Deck, newName: String) {
        if (deck.name.isNotBlank() && deck.name != newName) {
            decksRepository.updateDeck(deck.copy(name = newName))
        }
    }

    suspend fun deleteDeck(deck: Deck) {
        decksRepository.deleteDeck(deck)
        // TODO: also delete all children of this deck
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
