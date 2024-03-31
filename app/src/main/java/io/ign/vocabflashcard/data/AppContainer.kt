package io.ign.vocabflashcard.data

import android.content.Context
import io.ign.vocabflashcard.dataStore

interface AppContainer {
    val flashcardsRepository: FlashcardsRepository
    val cardsRepository: CardsRepository
    val userPrefsRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val flashcardsRepository: FlashcardsRepository by lazy {
        OfflineFlashcardsRepository(VocabFlashcardDatabase.getDatabase(context).flashcardDao())
    }

    override val cardsRepository: CardsRepository by lazy {
        OfflineCardsRepository(VocabFlashcardDatabase.getDatabase(context).cardDao())
    }

    override val userPrefsRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}