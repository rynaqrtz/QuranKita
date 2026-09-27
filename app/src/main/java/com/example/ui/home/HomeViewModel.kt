package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.LastRead
import com.example.data.model.PrayerTimeInfo
import com.example.data.model.Surah
import com.example.data.repository.QuranRepository
import com.example.util.PrayerSettings
import com.example.util.PrayerTimeCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

data class HomeUiState(
    val isAppLoading: Boolean = true,
    val selectedCityName: String = "Jakarta",
    val hijriDate: String = "",
    val prayerTimes: List<PrayerTimeInfo> = emptyList(),
    val nextPrayer: PrayerTimeInfo? = null,
    val totalKhatamCount: Int = 0,
    val totalMemorizedCount: Int = 0,
    val verseOfTheDay: VerseOfTheDay = VerseOfTheDay()
)

data class VerseOfTheDay(
    val surahName: String = "Al-Baqarah",
    val surahNumber: Int = 2,
    val verseNumber: Int = 286,
    val arabic: String = "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا",
    val translation: String = "Allah tidak membebani seseorang melainkan sesuai dengan kesanggupannya.",
    val tadabbur: String = "Setiap ujian dan tanggung jawab hidup kita telah diukur oleh Allah agar kita mampu melewatinya dengan sabar dan ikhtiar."
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val lastRead: StateFlow<LastRead?>
    val allSurahs: StateFlow<List<Surah>>

    private val verseOfDayReferences = listOf(
        2 to 286, 13 to 28, 61 to 4, 3 to 139, 29 to 69, 65 to 3,
        2 to 152, 67 to 2, 10 to 57, 48 to 29, 55 to 13, 94 to 5
    )

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuranRepository(db.quranDao())

        lastRead = repository.lastRead.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allSurahs = repository.allSurahs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initializeDatabase(application)
            refreshPrayerTimes()
            refreshVerseOfTheDay()
            observeProgress()
            _uiState.value = _uiState.value.copy(isAppLoading = false)

            while (isActive) {
                delay(60_000)
                refreshPrayerTimes()
                refreshVerseOfTheDay()
            }
        }
    }

    private fun refreshPrayerTimes() {
        val city = PrayerSettings.loadCity(getApplication<Application>())
        val times = PrayerTimeCalculator.calculatePrayerTimes(
            latitude = city.latitude,
            longitude = city.longitude,
            timeZoneOffset = city.timeZoneOffset,
            date = Date()
        )

        _uiState.value = _uiState.value.copy(
            selectedCityName = city.name,
            hijriDate = PrayerTimeCalculator.getTodayHijriDate(),
            prayerTimes = times,
            nextPrayer = times.firstOrNull { it.isNext }
        )
    }

    private suspend fun refreshVerseOfTheDay() {
        val today = Calendar.getInstance()
        val index = (
            today.get(Calendar.DAY_OF_YEAR) + today.get(Calendar.YEAR)
            ) % verseOfDayReferences.size
        val (surahNumber, verseNumber) = verseOfDayReferences[index]

        val verse = repository.getVerse(surahNumber, verseNumber) ?: return
        val surah = repository.getSurah(surahNumber).first()

        _uiState.value = _uiState.value.copy(
            verseOfTheDay = VerseOfTheDay(
                surahName = surah?.nameLatin ?: "Surah $surahNumber",
                surahNumber = surahNumber,
                verseNumber = verseNumber,
                arabic = verse.arabic,
                translation = verse.translation,
                tadabbur = verse.tafsir
            )
        )
    }

    private fun observeProgress() {
        viewModelScope.launch {
            allSurahs.collect { surahs ->
                _uiState.value = _uiState.value.copy(
                    totalKhatamCount = surahs.count { it.isKhatam }
                )
            }
        }
        viewModelScope.launch {
            repository.memorizedVerses.collect { memorized ->
                _uiState.value = _uiState.value.copy(
                    totalMemorizedCount = memorized.size
                )
            }
        }
    }
}
