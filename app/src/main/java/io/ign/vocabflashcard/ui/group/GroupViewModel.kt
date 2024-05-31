package io.ign.vocabflashcard.ui.group

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Card
import io.ign.vocabflashcard.data.GroupWithCardsList
import io.ign.vocabflashcard.data.GroupsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class GroupUiState(
    val id: Int = 0,
    val name: String = "",
    val cardList: List<Card> = listOf()
)

fun GroupWithCardsList.toGroupUiState(): GroupUiState {
    return GroupUiState(this.group.id, this.group.name, this.cardList)
}

class GroupViewModel(
    savedStateHandle: SavedStateHandle,
    private val groupsRepository: GroupsRepository
) : ViewModel() {
    private val groupId: Int = checkNotNull(savedStateHandle[GroupScreenDestination.idArg])

    val groupUiState: StateFlow<GroupUiState> =
        groupsRepository.getGroupWithCardsStream(groupId)
            .filterNotNull()
            .map { it.toGroupUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = GroupUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}