package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    suspend fun insertCard(card: Card)
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(card: Card)
    fun getCardStream(id: Int): Flow<Card?>
    fun getAllCardsStream(): Flow<List<Card>>
}
