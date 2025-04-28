package io.ign.vocabflashcard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("SELECT MAX(`order`) FROM decks")
    suspend fun getMaxOrder(): Int?

    @Query("UPDATE decks SET `order` = :order WHERE id = :id")
    suspend fun updateOrder(id: Int, order: Int)

    @Transaction
    suspend fun swapOrder(id1: Int, order1: Int, id2: Int, order2: Int) {
        updateOrder(id1, order2)
        updateOrder(id2, order1)
    }
}
