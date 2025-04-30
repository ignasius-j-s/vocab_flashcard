package io.ign.vocabflashcard.data

import androidx.room.Embedded
import androidx.room.Relation

data class CardData(
    @Embedded val card: Card,
//    TODO:
//    @Relation(entity = Translation::class, entityColumn = "card_id", parentColumn = "id")
//    val translationList: List<Translation> = emptyList(),
//    @Relation(entity = Example::class, entityColumn = "card_id", parentColumn = "id")
//    val exampleList: List<Example> = emptyList(),
)
