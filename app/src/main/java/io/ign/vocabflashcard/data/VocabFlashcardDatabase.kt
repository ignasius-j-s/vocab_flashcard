package io.ign.vocabflashcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Group::class, Card::class], version = 1, exportSchema = false)
abstract class VocabFlashcardDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var Instance: VocabFlashcardDatabase? = null

        fun getDatabase(context: Context): VocabFlashcardDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    VocabFlashcardDatabase::class.java, "vocabflashcard_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}