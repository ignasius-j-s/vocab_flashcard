package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

class OfflineFlashcardsRepository(private val flashcardDao: FlashcardDao) : FlashcardsRepository {
    override suspend fun insertFlashcard(flashcard: Flashcard) = flashcardDao.insert(flashcard)
    override suspend fun updateFlashcard(flashcard: Flashcard) = flashcardDao.update(flashcard)
    override suspend fun deleteFlashcard(flashcard: Flashcard) = flashcardDao.delete(flashcard)
    override fun getAllFlashcardsStream(): Flow<List<Flashcard>> = flashcardDao.getAllFlashcards()
    override fun getFlashcardStream(id: Int): Flow<Flashcard?> = flashcardDao.getFlashcard(id)
}