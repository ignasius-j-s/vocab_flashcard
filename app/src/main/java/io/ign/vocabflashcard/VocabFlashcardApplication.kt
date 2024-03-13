package io.ign.vocabflashcard

import android.app.Application
import io.ign.vocabflashcard.data.AppContainer
import io.ign.vocabflashcard.data.AppDataContainer

class VocabFlashcardApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}