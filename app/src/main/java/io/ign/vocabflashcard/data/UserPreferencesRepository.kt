package io.ign.vocabflashcard.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val DESCENDING = booleanPreferencesKey("descending")
    }

    val sortOrder: Flow<String> = dataStore.data.map { it[SORT_ORDER] ?: "Name" }
    val descending: Flow<Boolean> = dataStore.data.map { it[DESCENDING] ?: false }

    suspend fun saveSortOrder(value: String) {
        dataStore.edit { it[SORT_ORDER] = value }
    }

    suspend fun saveDescending(value: Boolean) {
        dataStore.edit { it[DESCENDING] = value }
    }
}