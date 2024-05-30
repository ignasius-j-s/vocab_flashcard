package io.ign.vocabflashcard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(card: Card)

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Query("SELECT * from cards ORDER BY word ASC")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * from cards WHERE id = :id")
    fun getCard(id: Int): Flow<Card>

    @Query("SELECT * from cards WHERE favorite = :value ORDER BY word ASC")
    fun getFavoriteCards(value: Boolean = true): Flow<List<Card>>

    @Query("SELECT COUNT(favorite) from cards WHERE favorite = :value")
    fun getFavoriteCardsCount(value: Boolean = true): Flow<Int>
}