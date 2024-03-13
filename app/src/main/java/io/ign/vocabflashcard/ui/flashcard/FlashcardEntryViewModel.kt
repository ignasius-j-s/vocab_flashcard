package io.ign.vocabflashcard.ui.flashcard

import io.ign.vocabflashcard.data.Flashcard

data class FlashcardUiState(
    val flashcard: Flashcard = Flashcard(0, ""),
    val isEntryValid: Boolean = false,
)

fun Flashcard.toFlashcardUiState(isEntryValid: Boolean): FlashcardUiState {
    return FlashcardUiState(this, isEntryValid)
}