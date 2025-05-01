package io.ign.vocabflashcard.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "decks", indices = [Index("order")])
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val order: Int = 0,
    val expanded: Boolean = false,
)
