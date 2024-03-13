package io.ign.vocabflashcard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class Translation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val translation: String,
    val type: String? = null,

    val cardId: Int,
)

enum class TranslationType {
    Noun, Verb, Adjective
}