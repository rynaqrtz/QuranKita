package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * Standard relational entity for Quran Chapters (Surahs).
 */
@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val nameLatin: String,
    val nameArabic: String,
    val meaning: String,
    val verseCount: Int,
    val revelation: String,
    val isBookmarked: Boolean = false,
    val isKhatam: Boolean = false,
    val memorizedVersesCount: Int = 0
)

/**
 * Full-Text Search (FTS) virtual table entity for Quran Chapters.
 * Enables high-performance full-text search over chapter names, Latin transliterations,
 * Arabic calligraphy titles, and Indonesian translation meanings.
 */
@Entity(tableName = "surahs_fts")
@Fts4
data class SurahFtsEntity(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Int,
    val number: Int,
    val nameLatin: String,
    val nameArabic: String,
    val meaning: String
)

/**
 * Standard relational entity for Quran Verses (Ayat).
 */
@Entity(tableName = "verses")
data class VerseEntity(
    @PrimaryKey val id: String, // Format: "${surahNumber}_${verseNumber}"
    val surahNumber: Int,
    val verseNumber: Int,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val tafsir: String = "",
    val isBookmarked: Boolean = false,
    val isMemorized: Boolean = false
)

/**
 * Full-Text Search (FTS) virtual table entity for Quran Verses.
 * Enables instant tokenized matching, prefix queries, and rank-ordered full-text search
 * across Arabic text, Latin transliteration, Indonesian translation, and Tafsir ringkas.
 */
@Entity(tableName = "verses_fts")
@Fts4
data class VerseFtsEntity(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Int,
    val surahNumber: Int,
    val verseNumber: Int,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val tafsir: String
)

/**
 * Entity tracking the user's latest reading position for quick resume.
 */
@Entity(tableName = "last_read")
data class LastReadEntity(
    @PrimaryKey val id: Int = 1,
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val timestamp: Long
)

/**
 * Entity tracking locally cached/downloaded murottal audio files for offline playback.
 */
@Entity(tableName = "downloaded_audios")
data class DownloadedAudioEntity(
    @PrimaryKey val key: String, // Format: "${qariId}_${surahNumber}"
    val qariId: String,
    val surahNumber: Int,
    val localFilePath: String,
    val fileSizeFormatted: String,
    val timestamp: Long
)
