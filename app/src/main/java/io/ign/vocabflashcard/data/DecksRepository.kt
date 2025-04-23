package io.ign.vocabflashcard.data

import io.ign.vocabflashcard.ui.setting.SortOrder
import kotlinx.coroutines.flow.Flow

interface DecksRepository {
    suspend fun insertDeck(deck: Deck)
    suspend fun updateDeck(deck: Deck)
    suspend fun deleteDeck(deck: Deck)
    fun getDeckStream(id: Int): Flow<Deck?>
    fun getAllDecksStream(sortOrder: SortOrder, isDescending: Boolean): Flow<List<Deck>>
    fun getDeckWithCardsStream(id: Int): Flow<DeckWithCards?>
}
