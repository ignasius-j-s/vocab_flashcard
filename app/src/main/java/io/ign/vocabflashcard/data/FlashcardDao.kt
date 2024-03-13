package io.ign.vocabflashcard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flashcard: Flashcard)

    @Update
    suspend fun update(flashcard: Flashcard)

    @Delete
    suspend fun delete(flashcard: Flashcard)

    @Query("SELECT * from flashcards ORDER BY name ASC")
    fun getAllFlashcards(): Flow<List<Flashcard>>
}