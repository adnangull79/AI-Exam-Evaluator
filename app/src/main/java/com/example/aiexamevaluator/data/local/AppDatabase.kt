package com.example.aiexamevaluator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// implementation "androidx.room:room-runtime"
// implementation "androidx.room:room-ktx"
// kapt "androidx.room:room-compiler"
@Database(
    entities = [EvaluationHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun evaluationDao(): EvaluationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "exameval_db",
                ).build().also { INSTANCE = it }
            }
        }
    }
}