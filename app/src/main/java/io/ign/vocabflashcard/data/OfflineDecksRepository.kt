package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

class OfflineDecksRepository(private val deckDao: DeckDao) : DecksRepository {
    override suspend fun insertDeck(deck: Deck) = deckDao.insert(deck)
    override suspend fun updateDeck(deck: Deck) = deckDao.update(deck)
    override suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck)

    override fun getDeckStream(id: Int): Flow<Deck?> = deckDao.get(id)
    override fun getAllDecksStream(): Flow<List<Deck>> = deckDao.getAll()
    override fun getDeckWithCardsStream(id: Int): Flow<DeckWithCards?> = deckDao.getWithCards(id)
    override fun getAllDeckWithCardsStream(): Flow<List<DeckWithCards>> = deckDao.getAllWithCards()

    override suspend fun updateDeckOrder(id: Int, order: Int) = deckDao.updateOrder(id, order)
    override fun getDeckMaxOrder(): Int? = deckDao.getMaxOrder()
}
