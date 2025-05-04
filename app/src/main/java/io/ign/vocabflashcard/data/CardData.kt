package io.ign.vocabflashcard.data

import androidx.room.Embedded
import androidx.room.Relation

data class CardData(
    @Embedded val card: Card,
    @Relation(entityColumn = "card_id", parentColumn = "id")
    val translationList: List<Translation> = emptyList(),
    @Relation(entityColumn = "card_id", parentColumn = "id")
    val usageList: List<Usage> = emptyList(),
)
