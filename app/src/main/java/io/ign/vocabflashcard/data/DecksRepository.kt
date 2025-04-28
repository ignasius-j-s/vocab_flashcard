package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

interface DecksRepository {
    suspend fun insertDeck(deck: Deck)
    suspend fun updateDeck(deck: Deck)
    suspend fun deleteDeck(deck: Deck)

    fun getDeckStream(id: Int): Flow<Deck?>
    fun getAllDecksStream(): Flow<List<Deck>>

    suspend fun updateDeckOrder(id: Int, order: Int)
    fun getDeckMaxOrder(): Int
}
