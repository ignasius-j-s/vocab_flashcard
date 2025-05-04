package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    suspend fun insertCard(card: Card)
    suspend fun upsertCard(card: Card)
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(card: Card)
    fun getCardStream(id: Int): Flow<Card?>
    fun getAllCardsStream(): Flow<List<Card>>
    fun getAllCardsInDeckStream(id: Int): Flow<List<Card>>
    fun getAllCardsInDeckStream(id: Int, query: String): Flow<List<Card>>

    fun getCardDataStream(id: Int): Flow<CardData?>
    fun getAllCardDataStream(): Flow<List<CardData>>
    suspend fun upsertCardData(cardData: CardData)

    suspend fun insertTranslations(translations: List<Translation>)
    suspend fun deleteTranslations(translations: List<Translation>)

    suspend fun upsertExamples(examples: List<Example>)
    suspend fun deleteExamples(examples: List<Example>)
}
