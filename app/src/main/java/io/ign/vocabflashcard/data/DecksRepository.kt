package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

interface DecksRepository {
    suspend fun insertDeck(deck: Deck)
    suspend fun updateDeck(deck: Deck)
    suspend fun deleteDeck(deck: Deck)

    fun getDeckStream(id: Int): Flow<Deck?>
    fun getAllDecksStream(): Flow<List<Deck>>

    suspend fun updateDeckOrder(id: Int, order: Int)
    suspend fun getMaxOrder(): Int?
    suspend fun swapDeckOrder(deck1: Deck, deck2: Deck): Unit
}
