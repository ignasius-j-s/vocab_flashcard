package io.ign.vocabflashcard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deck: Deck)

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Query("SELECT * from decks WHERE id = :id")
    fun get(id: Int): Flow<Deck>

    @Query(
        "SELECT * from decks ORDER BY " +
                "CASE WHEN :isDescending = 1 THEN name END DESC," +
                "CASE WHEN :isDescending = 0 THEN name END ASC"
    )
    fun getAllByName(isDescending: Boolean): Flow<List<Deck>>

    @Query(
        "SELECT * from decks ORDER BY " +
                "CASE WHEN :isDescending = 1 THEN created_at END DESC," +
                "CASE WHEN :isDescending = 0 THEN created_at END ASC"
    )
    fun getAllByTime(isDescending: Boolean): Flow<List<Deck>>

    @Query("SELECT * from decks WHERE id = :id")
    fun getWithCards(id: Int): Flow<DeckWithCards>
}
