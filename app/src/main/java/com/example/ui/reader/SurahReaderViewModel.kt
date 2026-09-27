package com.example.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Qari
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.repository.QuranRepository
import com.example.util.AudioPlaybackState
import com.example.util.AudioPlayerManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ReadingViewMode {
    PER_AYAT,
    MUSHAF
}

data class ReaderSettings(
    val viewMode: ReadingViewMode = ReadingViewMode.PER_AYAT,
    val isTajwidEnabled: Boolean = true,
    val isTransliterationEnabled: Boolean = true,
    val isTranslationEnabled: Boolean = true,
    val isTahfidzHideMode: Boolean = false, // If true, hides arabic until tapped
    val arabicFontSize: Float = 26f, // 20f .. 36f
)

@OptIn(ExperimentalCoroutinesApi::class)
class SurahReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository
    val audioPlayer: AudioPlayerManager = AudioPlayerManager(application)

    private val _currentSurahNumber = MutableStateFlow(1)
    val currentSurahNumber: StateFlow<Int> = _currentSurahNumber.asStateFlow()

    private val _readerSettings = MutableStateFlow(ReaderSettings())
    val readerSettings: StateFlow<ReaderSettings> = _readerSettings.asStateFlow()

    private val _revealedVerses = MutableStateFlow<Set<String>>(emptySet())
    val revealedVerses: StateFlow<Set<String>> = _revealedVerses.asStateFlow()

    private val _expandedTafsir = MutableStateFlow<Set<String>>(emptySet())
    val expandedTafsir: StateFlow<Set<String>> = _expandedTafsir.asStateFlow()

    val currentSurah: StateFlow<Surah?>
    val verses: StateFlow<List<Verse>>
    val playbackState: StateFlow<AudioPlaybackState> = audioPlayer.playbackState

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuranRepository(db.quranDao())

        currentSurah = _currentSurahNumber.flatMapLatest { repository.getSurah(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        verses = _currentSurahNumber.flatMapLatest { repository.getVersesForSurah(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        audioPlayer.onAudioDownloaded = { qariId, surahNumber, filePath, sizeStr ->
            repository.registerDownloadedAudio(qariId, surahNumber, filePath, sizeStr)
        }
    }

    fun downloadCurrentSurahAudio() {
        audioPlayer.downloadCurrentSurah(playbackState.value.totalVersesInSurah)
    }

    fun cancelSurahAudioDownload() {
        audioPlayer.cancelDownload()
    }

    fun loadSurah(surahNumber: Int) {
        _currentSurahNumber.value = surahNumber
    }

    fun setViewMode(mode: ReadingViewMode) {
        _readerSettings.value = _readerSettings.value.copy(viewMode = mode)
    }

    fun toggleTajwid() {
        _readerSettings.value = _readerSettings.value.copy(
            isTajwidEnabled = !_readerSettings.value.isTajwidEnabled
        )
    }

    fun toggleTransliteration() {
        _readerSettings.value = _readerSettings.value.copy(
            isTransliterationEnabled = !_readerSettings.value.isTransliterationEnabled
        )
    }

    fun toggleTahfidzHideMode() {
        _readerSettings.value = _readerSettings.value.copy(
            isTahfidzHideMode = !_readerSettings.value.isTahfidzHideMode
        )
        // Reset revealed
        _revealedVerses.value = emptySet()
    }

    fun toggleRevealVerse(verseId: String) {
        val current = _revealedVerses.value.toMutableSet()
        if (current.contains(verseId)) {
            current.remove(verseId)
        } else {
            current.add(verseId)
        }
        _revealedVerses.value = current
    }

    fun toggleExpandTafsir(verseId: String) {
        val current = _expandedTafsir.value.toMutableSet()
        if (current.contains(verseId)) {
            current.remove(verseId)
        } else {
            current.add(verseId)
        }
        _expandedTafsir.value = current
    }

    fun setArabicFontSize(size: Float) {
        _readerSettings.value = _readerSettings.value.copy(arabicFontSize = size)
    }

    fun toggleVerseBookmark(verse: Verse) {
        viewModelScope.launch {
            repository.toggleVerseBookmark(verse.id, verse.isBookmarked)
        }
    }

    fun toggleVerseMemorized(verse: Verse) {
        viewModelScope.launch {
            repository.toggleVerseMemorized(verse.id, verse.isMemorized)
        }
    }

    fun playVerse(verse: Verse, totalVerses: Int, surahName: String) {
        audioPlayer.playVerse(
            surahNumber = verse.surahNumber,
            surahName = surahName,
            verseNumber = verse.verseNumber,
            totalVerses = totalVerses
        )
    }

    fun playAll(surah: Surah) {
        audioPlayer.playVerse(
            surahNumber = surah.number,
            surahName = surah.nameLatin,
            verseNumber = 1,
            totalVerses = surah.verseCount
        )
    }

    fun cycleRepeatCount() {
        val current = audioPlayer.playbackState.value.targetRepeatCount
        val next = when (current) {
            1 -> 3
            3 -> 5
            5 -> 10
            else -> 1
        }
        audioPlayer.setTargetRepeatCount(next)
    }

    fun setQari(qari: Qari) {
        audioPlayer.setQari(qari)
    }

    fun updateLastRead(verseNumber: Int, surahName: String) {
        viewModelScope.launch {
            repository.setLastRead(_currentSurahNumber.value, surahName, verseNumber)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
