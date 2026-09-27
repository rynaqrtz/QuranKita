package com.example.data.repository

import android.content.Context
import com.example.data.local.dao.QuranDao
import com.example.data.local.entity.DownloadedAudioEntity
import com.example.data.local.entity.LastReadEntity
import com.example.data.local.entity.SurahEntity
import com.example.data.local.entity.SurahFtsEntity
import com.example.data.local.entity.VerseEntity
import com.example.data.local.entity.VerseFtsEntity
import com.example.data.model.LastRead
import com.example.data.model.Surah
import com.example.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File

class QuranRepository(private val quranDao: QuranDao) {

    suspend fun initializeDatabase(context: Context) = withContext(Dispatchers.IO) {
        if (quranDao.getSurahsCount() != 0) return@withContext

        // Load surahs.json
        try {
            val jsonString = context.assets.open("surahs.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val surahs = mutableListOf<SurahEntity>()
            val surahsFts = mutableListOf<SurahFtsEntity>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val sNo = obj.getInt("number")
                val sLatin = obj.getString("nameLatin")
                val sArabic = obj.getString("nameArabic")
                val sMeaning = obj.getString("meaning")
                surahs.add(
                    SurahEntity(
                        number = sNo,
                        nameLatin = sLatin,
                        nameArabic = sArabic,
                        meaning = sMeaning,
                        verseCount = obj.getInt("verseCount"),
                        revelation = obj.getString("revelation")
                    )
                )
                surahsFts.add(
                    SurahFtsEntity(
                        rowid = sNo,
                        number = sNo,
                        nameLatin = sLatin,
                        nameArabic = sArabic,
                        meaning = sMeaning
                    )
                )
            }
            quranDao.insertSurahs(surahs)
            quranDao.insertSurahsFts(surahsFts)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Stream verses: one verse per line, tab separated. Avoids holding the whole
        // payload as a parsed JSON tree, which used to spike tens of MB of heap.
        try {
            var verses = mutableListOf<VerseEntity>()
            var versesFts = mutableListOf<VerseFtsEntity>()
            context.assets.open("verses.txt").bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    if (line.isEmpty()) return@forEachLine
                    val parts = line.split('\t')
                    if (parts.size < 6) return@forEachLine
                    val surahNo = parts[0].toInt()
                    val vNum = parts[1].toInt()
                    val arabic = parts[2]
                    val translit = parts[3]
                    val translation = parts[4]
                    val tafsir = parts[5]
                    verses.add(
                        VerseEntity(
                            id = "${surahNo}_$vNum",
                            surahNumber = surahNo,
                            verseNumber = vNum,
                            arabic = arabic,
                            transliteration = translit,
                            translation = translation,
                            tafsir = tafsir
                        )
                    )
                    versesFts.add(
                        VerseFtsEntity(
                            rowid = surahNo * 1000 + vNum,
                            surahNumber = surahNo,
                            verseNumber = vNum,
                            arabic = arabic,
                            transliteration = translit,
                            translation = translation,
                            tafsir = tafsir
                        )
                    )
                    if (verses.size >= 300) {
                        quranDao.insertVerses(verses)
                        quranDao.insertVersesFts(versesFts)
                        verses = mutableListOf()
                        versesFts = mutableListOf()
                    }
                }
            }
            if (verses.isNotEmpty()) {
                quranDao.insertVerses(verses)
                quranDao.insertVersesFts(versesFts)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val allSurahs: Flow<List<Surah>> = quranDao.getAllSurahs().map { list ->
        list.map { it.toModel() }
    }.flowOn(Dispatchers.IO)

    val bookmarkedSurahs: Flow<List<Surah>> = quranDao.getBookmarkedSurahs().map { list ->
        list.map { it.toModel() }
    }.flowOn(Dispatchers.IO)

    val bookmarkedVerses: Flow<List<Verse>> = quranDao.getBookmarkedVerses().map { list ->
        list.map { it.toModel() }
    }.flowOn(Dispatchers.IO)

    val memorizedVerses: Flow<List<Verse>> = quranDao.getMemorizedVerses().map { list ->
        list.map { it.toModel() }
    }.flowOn(Dispatchers.IO)

    val lastRead: Flow<LastRead?> = quranDao.getLastRead().map { entity ->
        entity?.let {
            LastRead(
                surahNumber = it.surahNumber,
                surahName = it.surahName,
                verseNumber = it.verseNumber,
                timestamp = it.timestamp
            )
        }
    }.flowOn(Dispatchers.IO)

    fun getSurah(number: Int): Flow<Surah?> = quranDao.getSurahByNumber(number).map {
        it?.toModel()
    }.flowOn(Dispatchers.IO)

    fun getVersesForSurah(surahNumber: Int): Flow<List<Verse>> =
        quranDao.getVersesForSurah(surahNumber).map { list ->
            list.map { it.toModel() }
        }.flowOn(Dispatchers.IO)

    suspend fun getVerse(surahNumber: Int, verseNumber: Int): Verse? = withContext(Dispatchers.IO) {
        quranDao.getVerse(surahNumber, verseNumber)?.toModel()
    }

    fun searchVerses(query: String): Flow<List<Verse>> =
        quranDao.searchVerses(query).map { list ->
            list.map { it.toModel() }
        }.flowOn(Dispatchers.IO)

    fun searchVersesFts(query: String): Flow<List<Verse>> {
        val match = toFtsMatch(query)
        if (match.isEmpty()) return flowOf(emptyList())
        return quranDao.searchVersesFts(match).map { list ->
            list.map { it.toModel() }
        }.flowOn(Dispatchers.IO)
    }

    fun searchSurahsFts(query: String): Flow<List<Surah>> {
        val match = toFtsMatch(query)
        if (match.isEmpty()) return flowOf(emptyList())
        return quranDao.searchSurahsFts(match).map { list ->
            list.map { it.toModel() }
        }.flowOn(Dispatchers.IO)
    }

    fun searchSurahs(query: String): Flow<List<Surah>> =
        quranDao.searchSurahs(query).map { list ->
            list.map { it.toModel() }
        }.flowOn(Dispatchers.IO)

    private fun toFtsMatch(query: String): String =
        query.trim()
            .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { "$it*" }

    suspend fun setLastRead(surahNumber: Int, surahName: String, verseNumber: Int) = withContext(Dispatchers.IO) {
        quranDao.setLastRead(
            LastReadEntity(
                id = 1,
                surahNumber = surahNumber,
                surahName = surahName,
                verseNumber = verseNumber,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleSurahBookmark(surahNumber: Int, current: Boolean) = withContext(Dispatchers.IO) {
        quranDao.setSurahBookmark(surahNumber, !current)
    }

    suspend fun toggleSurahKhatam(surahNumber: Int, current: Boolean) = withContext(Dispatchers.IO) {
        quranDao.setSurahKhatam(surahNumber, !current)
    }

    suspend fun toggleVerseBookmark(verseId: String, current: Boolean) = withContext(Dispatchers.IO) {
        quranDao.setVerseBookmark(verseId, !current)
    }

    suspend fun toggleVerseMemorized(verseId: String, current: Boolean) = withContext(Dispatchers.IO) {
        quranDao.setVerseMemorized(verseId, !current)
    }

    val downloadedAudios: Flow<List<DownloadedAudioEntity>> = quranDao.getDownloadedAudios()

    suspend fun registerDownloadedAudio(qariId: String, surahNumber: Int, filePath: String, sizeStr: String) = withContext(Dispatchers.IO) {
        val key = "${qariId}_$surahNumber"
        quranDao.insertDownloadedAudio(
            DownloadedAudioEntity(
                key = key,
                qariId = qariId,
                surahNumber = surahNumber,
                localFilePath = filePath,
                fileSizeFormatted = sizeStr,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteDownloadedAudio(key: String, filePath: String) = withContext(Dispatchers.IO) {
        quranDao.deleteDownloadedAudio(key)
        try {
            File(filePath).deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun SurahEntity.toModel(): Surah = Surah(
        number = number,
        nameLatin = nameLatin,
        nameArabic = nameArabic,
        meaning = meaning,
        verseCount = verseCount,
        revelation = revelation,
        isBookmarked = isBookmarked,
        isKhatam = isKhatam,
        memorizedVersesCount = memorizedVersesCount
    )

    private fun VerseEntity.toModel(): Verse = Verse(
        id = id,
        surahNumber = surahNumber,
        verseNumber = verseNumber,
        arabic = arabic,
        transliteration = transliteration,
        translation = translation,
        tafsir = tafsir,
        isBookmarked = isBookmarked,
        isMemorized = isMemorized
    )
}
