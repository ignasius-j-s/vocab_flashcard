package io.ign.vocabflashcard

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.ign.vocabflashcard.data.AppContainer
import io.ign.vocabflashcard.data.AppDataContainer

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")

class VocabFlashcardApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}