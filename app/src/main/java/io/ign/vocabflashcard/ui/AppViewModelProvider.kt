package io.ign.vocabflashcard.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.ign.vocabflashcard.VocabFlashcardApplication
import io.ign.vocabflashcard.ui.home.HomeViewModel
import io.ign.vocabflashcard.ui.setting.SettingViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(vocabFlashcardApplication().container.groupsRepository)
        }

        initializer {
            SettingViewModel(vocabFlashcardApplication().container.userPrefsRepository)
        }
    }
}

fun CreationExtras.vocabFlashcardApplication(): VocabFlashcardApplication {
    return (this[AndroidViewModelFactory.APPLICATION_KEY] as VocabFlashcardApplication)
}