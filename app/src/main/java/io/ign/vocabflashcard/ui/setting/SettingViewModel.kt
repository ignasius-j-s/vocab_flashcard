package io.ign.vocabflashcard.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SettingUiState(val sortOrder: String, val descending: Boolean)

class SettingViewModel(private val userPrefsRepository: UserPreferencesRepository) : ViewModel() {
    private val availableSortOrder = listOf("Name", "Time")

    val settingUiState: StateFlow<SettingUiState> =
        userPrefsRepository.sortOrder.combine(userPrefsRepository.descending) { sort, desc ->
            SettingUiState(sort, desc)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SettingUiState("", false)
        )

    suspend fun saveSortOrder(sortOrder: String) {
        if (availableSortOrder.contains(sortOrder)) {
            userPrefsRepository.saveSortOrder(sortOrder)
        }
    }

    suspend fun saveDescending(descending: Boolean) {
        userPrefsRepository.saveDescending(descending)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}