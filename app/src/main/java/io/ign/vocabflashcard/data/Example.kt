package io.ign.vocabflashcard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "examples",
    foreignKeys = [ForeignKey(
        entity = Card::class,
        parentColumns = ["id"],
        childColumns = ["card_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("card_id")]
)
data class Example(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val example: String,
    @ColumnInfo(name = "card_id")
    val cardId: Int,
)
