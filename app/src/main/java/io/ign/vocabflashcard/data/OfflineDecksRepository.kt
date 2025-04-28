package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

class OfflineDecksRepository(private val deckDao: DeckDao) : DecksRepository {
    override suspend fun insertDeck(deck: Deck) = deckDao.insert(deck)
    override suspend fun updateDeck(deck: Deck) = deckDao.update(deck)
    override suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck)

    override fun getDeckStream(id: Int): Flow<Deck?> = deckDao.get(id)
    override fun getAllDecksStream(): Flow<List<Deck>> = deckDao.getAll()

    override suspend fun updateDeckOrder(id: Int, order: Int) = deckDao.updateOrder(id, order)
    override suspend fun getMaxOrder(): Int? = deckDao.getMaxOrder()
    override suspend fun swapDeckOrder(deck1: Deck, deck2: Deck) =
        deckDao.swapOrder(deck1.id, deck1.order, deck2.id, deck2.order)
}
