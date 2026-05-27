package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlantedTree::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun treeDao(): TreeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = try {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "tree_tracker_database_v6"
                    )
                        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE) // Avoid WAL shared memory issues in sandbox/container environments
                        .fallbackToDestructiveMigration(dropAllTables = true)
                        .build()
                } catch (e: Exception) {
                    try {
                        Room.inMemoryDatabaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java
                        )
                            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                            .fallbackToDestructiveMigration(dropAllTables = true)
                            .build()
                    } catch (ex: Exception) {
                        // Ultimate fallback: return a minimal implementation or rethrow
                        throw ex
                    }
                }
                INSTANCE = instance
                instance
            }
        }
    }
}
