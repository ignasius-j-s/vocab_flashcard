package io.ign.vocabflashcard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examples")
data class Example(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val example: String,
    @ColumnInfo(name = "card_id")
    val cardId: Int,
)
