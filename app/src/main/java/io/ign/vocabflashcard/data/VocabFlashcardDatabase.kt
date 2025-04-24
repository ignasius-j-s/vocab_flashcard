package io.ign.vocabflashcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

@Database(entities = [Deck::class, Card::class], version = 1, exportSchema = false)
abstract class VocabFlashcardDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var Instance: VocabFlashcardDatabase? = null

        fun getDatabase(context: Context): VocabFlashcardDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    VocabFlashcardDatabase::class.java, "vocabflashcard-db"
                )
                    .fallbackToDestructiveMigration(false)
                    .addCallback(object: Callback() {
                        override fun onOpen(connection: SQLiteConnection) {
                            super.onOpen(connection)
                            connection.execSQL("PRAGMA foreign_keys=ON")
                        }
                    })
//                    .createFromAsset("app.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
