package io.ign.vocabflashcard.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "decks", indices = [Index("order")])
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val order: Int = 0,
    val expanded: Boolean = false
) {
    fun isValid(): Boolean {
        return this.name.isNotBlank()
    }
}

data class DeckData(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "id",
        entityColumn = "deck_id"
    )
    val cards: List<Card>
)
