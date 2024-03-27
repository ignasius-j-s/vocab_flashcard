package io.ign.vocabflashcard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    val note: String,
    val definition: String,

    val flashcardId: Int,
)
