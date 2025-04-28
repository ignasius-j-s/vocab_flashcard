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
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deck: Deck)

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Query("SELECT * from decks WHERE id = :id")
    fun get(id: Int): Flow<Deck>

    @Query("SELECT * from decks ORDER BY `order` ASC")
    fun getAll(): Flow<List<Deck>>

    @Query("UPDATE decks SET `order` = :order WHERE id = :id")
    suspend fun updateOrder(id: Int, order: Int)

    @Query("SELECT IFNULL(MAX(`order`), 0) FROM decks")
    fun getMaxOrder(): Int
}
