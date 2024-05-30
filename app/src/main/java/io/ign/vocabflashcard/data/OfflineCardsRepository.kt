package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

class OfflineCardsRepository(private val cardDao: CardDao) : CardsRepository {
    override suspend fun insert(card: Card) = cardDao.insert(card)
    override suspend fun update(card: Card) = cardDao.update(card)
    override suspend fun delete(card: Card) = cardDao.delete(card)
    override fun getAllCardsStream(): Flow<List<Card>> = cardDao.getAllCards()
    override fun getCardStream(id: Int): Flow<Card?> = cardDao.getCard(id)
    override fun getFavoriteCardsStream(): Flow<List<Card>> = cardDao.getFavoriteCards()
    override fun getFavoriteCardsCount(): Flow<Int> = cardDao.getFavoriteCardsCount()
}