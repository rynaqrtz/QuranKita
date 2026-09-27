package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.LastRead
import com.example.data.model.PrayerTimeInfo
import com.example.data.model.Surah
import com.example.data.repository.QuranRepository
import com.example.util.PrayerTimeCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
            val startTime = System.currentTimeMillis()
            repository.initializeDatabase(application)
            refreshPrayerTimes()
            observeProgress()
            val elapsed = System.currentTimeMillis() - startTime
            val remaining = 900L - elapsed
            if (remaining > 0) {
                kotlinx.coroutines.delay(remaining)
            }
            _uiState.value = _uiState.value.copy(isAppLoading = false)
        }
    }

    private fun refreshPrayerTimes() {
        val city = PrayerTimeCalculator.DEFAULT_CITIES.first()
        val times = PrayerTimeCalculator.calculatePrayerTimes(
            latitude = city.latitude,
            longitude = city.longitude,
            timeZoneOffset = city.timeZoneOffset,
            date = Date()
        )
        val next = times.firstOrNull { it.isNext }

        _uiState.value = _uiState.value.copy(
            selectedCityName = city.name,
            hijriDate = PrayerTimeCalculator.getTodayHijriDate(),
            prayerTimes = times,
            nextPrayer = next
        )
    }

    private fun observeProgress() {
        viewModelScope.launch {
            allSurahs.collect { surahs ->
                val khatam = surahs.count { it.isKhatam }
                _uiState.value = _uiState.value.copy(
                    totalKhatamCount = khatam
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
