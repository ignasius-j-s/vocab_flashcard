package io.ign.vocabflashcard.data

import io.ign.vocabflashcard.ui.setting.SortOrder
import kotlinx.coroutines.flow.Flow

class OfflineDecksRepository(private val deckDao: DeckDao) : DecksRepository {
    override suspend fun insertDeck(deck: Deck) = deckDao.insert(deck)
    override suspend fun updateDeck(deck: Deck) = deckDao.update(deck)
    override suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck)
    override fun getDeckStream(id: Int): Flow<Deck?> = deckDao.get(id)
    override fun getAllDecksStream(
        sortOrder: SortOrder,
        isDescending: Boolean
    ): Flow<List<Deck>> {
        return when (sortOrder) {
            SortOrder.NAME -> deckDao.getAllByName(isDescending)
            SortOrder.TIME -> deckDao.getAllByTime(isDescending)
        }
    }
    override fun getDeckWithCardsStream(id: Int): Flow<DeckWithCards?> =
        deckDao.getWithCards(id)
}
