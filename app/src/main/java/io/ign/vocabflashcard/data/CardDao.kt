package io.ign.vocabflashcard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(card: Card): Long

    @Upsert
    suspend fun upsert(card: Card)

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Query("SELECT * from cards WHERE id = :id")
    fun get(id: Int): Flow<Card>

    @Query("SELECT * from cards ORDER BY term ASC")
    fun getAll(): Flow<List<Card>>

    @Query("SELECT * from cards WHERE deck_id = :id ORDER BY term ASC")
    fun getAllInDeck(id: Int): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE deck_id = :id AND term LIKE :query ORDER BY `term` ASC")
    fun getAllInDeck(id: Int, query: String): Flow<List<Card>>

    @Transaction
    @Query("SELECT * from cards WHERE id = :id")
    fun getData(id: Int): Flow<CardData>

    @Transaction
    @Query("SELECT * from cards ORDER BY term ASC")
    fun getAllData(): Flow<List<CardData>>

    @Transaction
    suspend fun upsertData(cardData: CardData) {
        if (cardData.card.id == 0) {
            val cardId = insert(cardData.card).toInt()
            upsertTranslations(cardData.translationList.map { it.copy(cardId = cardId) })
            upsertExamples(cardData.exampleList.map { it.copy(cardId = cardId) })
        } else {
            update(cardData.card)
            upsertTranslations(cardData.translationList)
            upsertExamples(cardData.exampleList)
        }
    }

    @Upsert
    suspend fun upsertTranslations(translation: List<Translation>)

    @Delete
    suspend fun deleteTranslations(translations: List<Translation>)

    @Upsert
    suspend fun upsertExamples(examples: List<Example>)

    @Delete
    suspend fun deleteExamples(examples: List<Example>)
}
