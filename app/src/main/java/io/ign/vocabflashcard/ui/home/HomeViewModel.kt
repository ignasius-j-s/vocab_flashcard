package io.ign.vocabflashcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Flashcard
import io.ign.vocabflashcard.data.FlashcardsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(val flashcardList: List<Flashcard> = listOf())

class HomeViewModel(private val flashcardRepository: FlashcardsRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        flashcardRepository.getAllFlashcardsStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    suspend fun saveFlashcard(flashcard: Flashcard) {
        if (flashcard.name.isNotBlank()) {
            flashcardRepository.insertFlashcard(flashcard)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}