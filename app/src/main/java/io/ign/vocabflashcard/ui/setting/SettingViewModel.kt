package io.ign.vocabflashcard.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ign.vocabflashcard.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SettingUiState(val sortOrder: SortOrder, val descending: Boolean)
enum class SortOrder {
    NAME,
    TIME
}

class SettingViewModel(private val userPrefsRepository: UserPreferencesRepository) : ViewModel() {
    val settingUiState: StateFlow<SettingUiState> =
        userPrefsRepository.getUserPrefs().map {
            SettingUiState(it.sortOrder, it.descending)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SettingUiState(SortOrder.NAME, false)
        )

    suspend fun saveSortOrder(sortOrder: SortOrder) {
        userPrefsRepository.saveSortOrder(sortOrder)
    }

    suspend fun saveDescending(descending: Boolean) {
        userPrefsRepository.saveDescending(descending)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
