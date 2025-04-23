package io.ign.vocabflashcard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class Translation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val translation: String,
    @ColumnInfo(name = "card_id")
    val cardId: Int,
)
