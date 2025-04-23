package io.ign.vocabflashcard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val term: String,
    val note: String,
    val definition: String,
    @ColumnInfo(name = "deck_id")
    val deckId: Int,
)
