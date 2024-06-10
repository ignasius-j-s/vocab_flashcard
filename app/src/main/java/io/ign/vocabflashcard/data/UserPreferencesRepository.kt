package io.ign.vocabflashcard.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ign.vocabflashcard.ui.setting.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

data class UserPrefs(val sortOrder: String, val isDescending: Boolean)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val DESCENDING = booleanPreferencesKey("descending")
    }

    fun getUserPrefs(): Flow<UserPrefs> {
        return dataStore.data.map { it[SORT_ORDER] ?: "NAME" }
            .combine(dataStore.data.map { it[DESCENDING] ?: false }) { sortOrder, isDescending ->
                UserPrefs(sortOrder, isDescending)
            }
    }

    suspend fun saveSortOrder(value: SortOrder) {
        dataStore.edit { it[SORT_ORDER] = value.name }
    }

    suspend fun saveDescending(value: Boolean) {
        dataStore.edit { it[DESCENDING] = value }
    }
}