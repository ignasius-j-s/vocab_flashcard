package io.ign.vocabflashcard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examples")
data class UsageExample(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val example: String,

    val cardId: Int,
)