package io.ign.vocabflashcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.CardsRepository
import io.ign.vocabflashcard.data.Group
import io.ign.vocabflashcard.data.GroupsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(val groupList: List<Group> = listOf(), val favFlashcardCount: Int = 0)

class HomeViewModel(
    private val groupsRepository: GroupsRepository,
    private val cardsRepository: CardsRepository
) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        groupsRepository.getAllGroupsStream()
            .combine(cardsRepository.getFavoriteCardsCount()) { groupList, favFlashcardCount ->
                HomeUiState(
                    groupList,
                    favFlashcardCount
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    suspend fun saveGroup(group: Group) {
        if (group.name.isNotBlank()) {
            groupsRepository.insertGroup(group)
        }
    }

    suspend fun editGroup(group: Group, newName: String) {
        if (group.name.isNotBlank() && group.name != newName) {
            groupsRepository.updateGroup(group.copy(name = newName))
        }
    }

    suspend fun deleteGroup(group: Group) {
        groupsRepository.deleteGroup(group)
        // TODO: also delete all children of this group
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}