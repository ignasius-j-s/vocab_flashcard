package io.ign.vocabflashcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.Group
import io.ign.vocabflashcard.data.GroupsRepository
import io.ign.vocabflashcard.data.UserPreferencesRepository
import io.ign.vocabflashcard.data.UserPrefs
import io.ign.vocabflashcard.ui.setting.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(val groupList: List<Group> = listOf())

class HomeViewModel(
    private val groupsRepository: GroupsRepository,
    userPrefsRepository: UserPreferencesRepository
) : ViewModel() {
    private val userPrefs: StateFlow<UserPrefs> =
        userPrefsRepository.getUserPrefs().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = UserPrefs("NAME", false)
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val groupList = userPrefs.flatMapLatest {
        groupsRepository.getAllGroupsStream(SortOrder.valueOf(it.sortOrder), it.isDescending)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    private val _homeUiState = MutableStateFlow(HomeUiState())

    val homeUiState: StateFlow<HomeUiState> =
        combine(_homeUiState, groupList, userPrefs) { homeUiState, groupList, _ ->
            homeUiState.copy(groupList = groupList)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    suspend fun saveGroup(group: Group) {
        if (group.name.isNotBlank()) {
            val newGroup = group.copy(createdAt = System.currentTimeMillis())
            groupsRepository.insertGroup(newGroup)
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