package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DownloadedAudioEntity
import com.example.data.local.entity.LastReadEntity
import com.example.data.local.entity.SurahEntity
import com.example.data.local.entity.SurahFtsEntity
import com.example.data.local.entity.VerseEntity
import com.example.data.local.entity.VerseFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {

    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE number = :number")
    fun getSurahByNumber(number: Int): Flow<SurahEntity?>

    @Query("SELECT * FROM surahs WHERE isBookmarked = 1 ORDER BY number ASC")
    fun getBookmarkedSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Update
    suspend fun updateSurah(surah: SurahEntity)

    @Query("UPDATE surahs SET isBookmarked = :isBookmarked WHERE number = :surahNumber")
    suspend fun setSurahBookmark(surahNumber: Int, isBookmarked: Boolean)

    @Query("UPDATE surahs SET isKhatam = :isKhatam WHERE number = :surahNumber")
    suspend fun setSurahKhatam(surahNumber: Int, isKhatam: Boolean)

    // FTS for Surahs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahsFts(surahs: List<SurahFtsEntity>)

    @Query("""
        SELECT surahs.* FROM surahs
        JOIN surahs_fts ON surahs_fts.number = surahs.number
        WHERE surahs_fts MATCH :query
        ORDER BY surahs.number ASC
    """)
    fun searchSurahsFts(query: String): Flow<List<SurahEntity>>

    // Verses
    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber ORDER BY verseNumber ASC")
    fun getVersesForSurah(surahNumber: Int): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE surahNumber = :surahNumber AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getVerse(surahNumber: Int, verseNumber: Int): VerseEntity?

    @Query("SELECT * FROM verses WHERE isBookmarked = 1 ORDER BY surahNumber ASC, verseNumber ASC")
    fun getBookmarkedVerses(): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE isMemorized = 1 ORDER BY surahNumber ASC, verseNumber ASC")
    fun getMemorizedVerses(): Flow<List<VerseEntity>>

    // Full-Text Search (FTS) for Verses
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersesFts(verses: List<VerseFtsEntity>)

    @Query("""
        SELECT verses.* FROM verses
        JOIN verses_fts ON verses_fts.surahNumber = verses.surahNumber AND verses_fts.verseNumber = verses.verseNumber
        WHERE verses_fts MATCH :query
        LIMIT 100
    """)
    fun searchVersesFts(query: String): Flow<List<VerseEntity>>

    // Relational substring search (fallback)
    @Query("""
        SELECT * FROM verses 
        WHERE translation LIKE '%' || :query || '%' 
           OR transliteration LIKE '%' || :query || '%' 
           OR arabic LIKE '%' || :query || '%' 
        LIMIT 100
    """)
    fun searchVerses(query: String): Flow<List<VerseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    @Query("UPDATE verses SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun setVerseBookmark(id: String, isBookmarked: Boolean)

    @Query("UPDATE verses SET isMemorized = :isMemorized WHERE id = :id")
    suspend fun setVerseMemorized(id: String, isMemorized: Boolean)

    // Last Read
    @Query("SELECT * FROM last_read WHERE id = 1")
    fun getLastRead(): Flow<LastReadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setLastRead(lastRead: LastReadEntity)

    // Downloaded Audios
    @Query("SELECT * FROM downloaded_audios ORDER BY timestamp DESC")
    fun getDownloadedAudios(): Flow<List<DownloadedAudioEntity>>

    @Query("SELECT * FROM downloaded_audios WHERE key = :key")
    suspend fun getDownloadedAudioByKey(key: String): DownloadedAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedAudio(audio: DownloadedAudioEntity)

    @Query("DELETE FROM downloaded_audios WHERE key = :key")
    suspend fun deleteDownloadedAudio(key: String)

    @Query("DELETE FROM downloaded_audios")
    suspend fun clearAllDownloadedAudios()
}
