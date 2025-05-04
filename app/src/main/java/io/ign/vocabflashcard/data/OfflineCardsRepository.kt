package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

class OfflineCardsRepository(private val cardDao: CardDao) : CardsRepository {
    override suspend fun insertCard(card: Card) {
        cardDao.insert(card)
    }
    override suspend fun upsertCard(card: Card) = cardDao.upsert(card)
    override suspend fun updateCard(card: Card) = cardDao.update(card)
    override suspend fun deleteCard(card: Card) = cardDao.delete(card)
    override fun getCardStream(id: Int): Flow<Card?> = cardDao.get(id)
    override fun getAllCardsStream(): Flow<List<Card>> = cardDao.getAll()
    override fun getAllCardsInDeckStream(id: Int): Flow<List<Card>> = cardDao.getAllInDeck(id)
    override fun getAllCardsInDeckStream(id: Int, query: String) = cardDao.getAllInDeck(id, query)

    override fun getCardDataStream(id: Int): Flow<CardData?> = cardDao.getData(id)
    override fun getAllCardDataStream(): Flow<List<CardData>> = cardDao.getAllData()
    override suspend fun upsertCardData(cardData: CardData) = cardDao.upsertData(cardData)

    override suspend fun insertTranslations(translations: List<Translation>) =
        cardDao.upsertTranslations(translations)

    override suspend fun deleteTranslations(translations: List<Translation>) =
        cardDao.deleteTranslations(translations)

    override suspend fun upsertExamples(examples: List<Example>) = cardDao.upsertExamples(examples)
    override suspend fun deleteExamples(examples: List<Example>) = cardDao.deleteExamples(examples)
}
