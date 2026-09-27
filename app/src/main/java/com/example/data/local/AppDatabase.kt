package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.QuranDao
import com.example.data.local.entity.DownloadedAudioEntity
import com.example.data.local.entity.LastReadEntity
import com.example.data.local.entity.SurahEntity
import com.example.data.local.entity.SurahFtsEntity
import com.example.data.local.entity.VerseEntity
import com.example.data.local.entity.VerseFtsEntity

@Database(
    entities = [
        SurahEntity::class,
        SurahFtsEntity::class,
        VerseEntity::class,
        VerseFtsEntity::class,
        LastReadEntity::class,
        DownloadedAudioEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quranDao(): QuranDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qurankita_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
