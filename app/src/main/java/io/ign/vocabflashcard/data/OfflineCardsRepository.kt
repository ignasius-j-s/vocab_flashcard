package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

class OfflineCardsRepository(private val cardDao: CardDao) : CardsRepository {
    override suspend fun insertCard(card: Card) = cardDao.insert(card)
    override suspend fun updateCard(card: Card) = cardDao.update(card)
    override suspend fun deleteCard(card: Card) = cardDao.delete(card)
    override fun getCardStream(id: Int): Flow<Card?> = cardDao.get(id)
    override fun getAllCardsStream(): Flow<List<Card>> = cardDao.getAllCards()
}
