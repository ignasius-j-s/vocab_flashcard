package io.ign.vocabflashcard.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flashcard: Group)

    @Update
    suspend fun update(flashcard: Group)

    @Delete
    suspend fun delete(flashcard: Group)

    @Query(
        "SELECT * from groups ORDER BY " +
                "CASE WHEN :isDesc = 1 THEN name END DESC," +
                "CASE WHEN :isDesc = 0 THEN name END ASC"
    )
    fun getAllGroupsByName(isDesc: Boolean): Flow<List<Group>>

    @Query(
        "SELECT * from groups ORDER BY " +
                "CASE WHEN :isDesc = 1 THEN createdAt END DESC," +
                "CASE WHEN :isDesc = 0 THEN createdAt END ASC"
    )
    fun getAllGroupsByTime(isDesc: Boolean): Flow<List<Group>>
    
    @Query("SELECT * from groups WHERE id = :id")
    fun getGroup(id: Int): Flow<Group>

    @Query("SELECT * from groups WHERE id = :id")
    fun getGroupWithCardsList(id: Int): Flow<GroupWithCardsList>
}