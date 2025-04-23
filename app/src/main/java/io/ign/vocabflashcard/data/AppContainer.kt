package io.ign.vocabflashcard.data

import android.content.Context
import io.ign.vocabflashcard.dataStore

interface AppContainer {
    val decksRepository: DecksRepository
    val cardsRepository: CardsRepository
    val userPrefsRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val decksRepository: DecksRepository by lazy {
        OfflineDecksRepository(VocabFlashcardDatabase.getDatabase(context).deckDao())
    }

    override val cardsRepository: CardsRepository by lazy {
        OfflineCardsRepository(VocabFlashcardDatabase.getDatabase(context).cardDao())
    }

    override val userPrefsRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}
