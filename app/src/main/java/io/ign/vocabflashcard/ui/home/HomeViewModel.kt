package io.ign.vocabflashcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Card
import io.ign.vocabflashcard.data.CardData
import io.ign.vocabflashcard.data.CardsRepository
import io.ign.vocabflashcard.data.Deck
import io.ign.vocabflashcard.data.DecksRepository
import io.ign.vocabflashcard.data.Usage
import io.ign.vocabflashcard.data.Translation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class DialogKind {
    object None : DialogKind()
    object Create : DialogKind()
    class Rename(val renamedDeck: Deck) : DialogKind()
    class Remove(val deletedDeck: Deck) : DialogKind()
    class Menu(val selectedDeck: Deck, val deckIndex: Int) : DialogKind()
}

sealed class CardViewKind {
    object None : CardViewKind()
    class Create(val deckId: Int) : CardViewKind()
    class Edit(val card: Card) : CardViewKind()
    class Remove(val card: Card) : CardViewKind()
    class Show(val card: Card) : CardViewKind()
}

class HomeViewModel(
    private val decksRepository: DecksRepository,
    private val cardsRepository: CardsRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val deckListState: StateFlow<List<Deck>> = decksRepository.getAllDecksStream()
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    private val _cardViewState = MutableStateFlow(CardViewKind.None as CardViewKind)
    val cardViewState: StateFlow<CardViewKind> = _cardViewState

    private val _deckDialogState = MutableStateFlow(DialogKind.None as DialogKind)
    val deckDialogState: StateFlow<DialogKind> = _deckDialogState

    fun insertDeck(deck: Deck) {
        viewModelScope.launch {
            val maxOrder = decksRepository.getMaxOrder() ?: 0
            decksRepository.insertDeck(deck.copy(order = maxOrder + 1))
        }
    }

    fun updateDeck(deck: Deck) {
        viewModelScope.launch { decksRepository.updateDeck(deck) }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch { decksRepository.deleteDeck(deck) }
    }

    fun swapDeckOrder(deck1: Deck, deck2: Deck) {
        viewModelScope.launch { decksRepository.swapDeckOrder(deck1, deck2) }
    }

    fun fetchCardsStream(deckId: Int, searchQuery: String? = null): Flow<List<Card>> {
        return if (searchQuery.isNullOrBlank()) {
            cardsRepository.getAllCardsInDeckStream(deckId)
        } else {
            cardsRepository.getAllCardsInDeckStream(deckId, "%$searchQuery%")
        }
    }

    fun fetchCardDataStream(id: Int): Flow<CardData?> = cardsRepository.getCardDataStream(id)

    fun hideDeckDialog() {
        _deckDialogState.value = DialogKind.None
    }

    fun createDeckDialog() {
        _deckDialogState.value = DialogKind.Create
    }

    fun renameDeckDialog(deck: Deck) {
        _deckDialogState.value = DialogKind.Rename(deck)
    }

    fun deleteDeckDialog(deck: Deck) {
        _deckDialogState.value = DialogKind.Remove(deck)
    }

    fun menuDeckDialog(deck: Deck, index: Int) {
        _deckDialogState.value = DialogKind.Menu(deck, index)
    }

    fun showCard(card: Card) {
        _cardViewState.value = CardViewKind.Show(card)
    }

    fun editCard(card: Card) {
        _cardViewState.value = CardViewKind.Edit(card)
    }

    fun removeCard(card: Card) {
        _cardViewState.value = CardViewKind.Remove(card)
    }

    fun newCard(deckId: Int) {
        _cardViewState.value = CardViewKind.Create(deckId)
    }

    fun hideCardView() {
        _cardViewState.value = CardViewKind.None
    }

    fun saveCardData(cardData: CardData) {
        viewModelScope.launch { cardsRepository.upsertCardData(cardData) }
    }

    fun deleteCardTranslations(translations: List<Translation>) {
        viewModelScope.launch { cardsRepository.deleteTranslations(translations) }
    }

    fun deleteCardUsages(usages: List<Usage>) {
        viewModelScope.launch { cardsRepository.deleteUsages(usages) }
    }
}
