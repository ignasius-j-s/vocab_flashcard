package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    suspend fun insert(card: Card)
    suspend fun update(card: Card)
    suspend fun delete(card: Card)
    fun getAllCardsStream(): Flow<List<Card>>
    fun getCardStream(id: Int): Flow<Card?>
    fun getFavoriteCardsStream(): Flow<List<Card>>
    fun getFavoriteCardsCount(): Flow<Int>

}