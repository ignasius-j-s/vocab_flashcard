package io.ign.vocabflashcard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = ["id"],
        childColumns = ["deck_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("deck_id")]
)
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val term: String,
    val description: String,
    val note: String = "",
    @ColumnInfo(name = "deck_id")
    val deckId: Int,
)
