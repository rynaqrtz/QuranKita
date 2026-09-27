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
import org.json.JSONObject
import java.io.File

class QuranRepository(private val quranDao: QuranDao) {

    suspend fun initializeDatabase(context: Context) = withContext(Dispatchers.IO) {
        val count = quranDao.getSurahsCount()
        if (count == 0) {
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

            // Load initial_verses.json
            try {
                val versesJson = context.assets.open("initial_verses.json").bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(versesJson)
                val versesList = mutableListOf<VerseEntity>()
                val versesFtsList = mutableListOf<VerseFtsEntity>()
                val keys = jsonObj.keys()
                while (keys.hasNext()) {
                    val surahNoStr = keys.next()
                    val surahNo = surahNoStr.toInt()
                    val arr = jsonObj.getJSONArray(surahNoStr)
                    for (j in 0 until arr.length()) {
                        val vObj = arr.getJSONObject(j)
                        val vNum = vObj.getInt("verseNumber")
                        val vArabic = vObj.getString("arabic")
                        val vTrans = vObj.getString("transliteration")
                        val vTranslation = vObj.getString("translation")
                        val vTafsir = vObj.optString("tafsir", "")
                        versesList.add(
                            VerseEntity(
                                id = "${surahNo}_$vNum",
                                surahNumber = surahNo,
                                verseNumber = vNum,
                                arabic = vArabic,
                                transliteration = vTrans,
                                translation = vTranslation,
                                tafsir = vTafsir
                            )
                        )
                        versesFtsList.add(
                            VerseFtsEntity(
                                rowid = surahNo * 1000 + vNum,
                                surahNumber = surahNo,
                                verseNumber = vNum,
                                arabic = vArabic,
                                transliteration = vTrans,
                                translation = vTranslation,
                                tafsir = vTafsir
                            )
                        )
                    }
                }
                quranDao.insertVerses(versesList)
                quranDao.insertVersesFts(versesFtsList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
