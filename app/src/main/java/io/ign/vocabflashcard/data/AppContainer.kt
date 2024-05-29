package io.ign.vocabflashcard.data

import android.content.Context
import io.ign.vocabflashcard.dataStore

interface AppContainer {
    val groupsRepository: GroupsRepository
    val cardsRepository: CardsRepository
    val userPrefsRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val groupsRepository: GroupsRepository by lazy {
        OfflineGroupsRepository(VocabFlashcardDatabase.getDatabase(context).groupDao())
    }

    override val cardsRepository: CardsRepository by lazy {
        OfflineCardsRepository(VocabFlashcardDatabase.getDatabase(context).cardDao())
    }

    override val userPrefsRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}