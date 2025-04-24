package io.ign.vocabflashcard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "translations",
    foreignKeys = [ForeignKey(
        entity = Card::class,
        parentColumns = ["id"],
        childColumns = ["card_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("card_id")]
)
data class Translation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val translation: String,
    @ColumnInfo(name = "card_id")
    val cardId: Int,
)
