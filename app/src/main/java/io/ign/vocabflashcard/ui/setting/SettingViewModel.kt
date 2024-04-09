package io.ign.vocabflashcard.ui.setting

import androidx.lifecycle.ViewModel
import io.ign.vocabflashcard.data.FlashcardsRepository
import io.ign.vocabflashcard.data.UserPreferencesRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

data class SettingUiState(val sortOrder: String, val descending: Boolean)

class SettingViewModel(private val userPrefsRepository: UserPreferencesRepository) : ViewModel() {
    private val availableSortOrder = listOf("Name", "Time")
    suspend fun saveSortOrder(sortOrder: String) {
        if (availableSortOrder.contains(sortOrder)) {
            userPrefsRepository.saveSortOrder(sortOrder)
        }
    }

    suspend fun saveDescending(descending: Boolean) {
        userPrefsRepository.saveDescending(descending)
    }
}