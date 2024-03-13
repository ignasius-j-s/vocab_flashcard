package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

interface FlashcardsRepository {
    suspend fun insertFlashcard(flashcard: Flashcard)
    suspend fun updateFlashcard(flashcard: Flashcard)
    suspend fun deleteFlashcard(flashcard: Flashcard)
    fun getAllFlashcardsStream(): Flow<List<Flashcard>>
}