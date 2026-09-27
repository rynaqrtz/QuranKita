package com.example.ui.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.JuzInfo
import com.example.data.model.LastRead
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class QuranTab {
    SURAH,
    JUZ,
    BOOKMARK
}

class QuranListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository

    private val _selectedTab = MutableStateFlow(QuranTab.SURAH)
    val selectedTab: StateFlow<QuranTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val allSurahs: StateFlow<List<Surah>>
    val bookmarkedSurahs: StateFlow<List<Surah>>
    val bookmarkedVerses: StateFlow<List<Verse>>
    val lastRead: StateFlow<LastRead?>

    val juzList: List<JuzInfo> = JuzInfo.ALL_JUZ

    val readingProgress: StateFlow<Float>

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuranRepository(db.quranDao())

        allSurahs = combine(repository.allSurahs, _searchQuery) { list, query ->
            if (query.isBlank()) {
                list
            } else {
                list.filter {
                    it.nameLatin.contains(query, ignoreCase = true) ||
                            it.nameArabic.contains(query, ignoreCase = true) ||
                            it.meaning.contains(query, ignoreCase = true) ||
                            it.number.toString() == query.trim()
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        bookmarkedSurahs = repository.bookmarkedSurahs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        bookmarkedVerses = repository.bookmarkedVerses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        lastRead = repository.lastRead.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        readingProgress = combine(repository.allSurahs, repository.lastRead) { surahs, last ->
            val khatamCount = surahs.count { it.isKhatam }
            val lastReadFraction = if (last != null && last.surahNumber in 1..114) {
                last.surahNumber.toFloat() / 114f
            } else 0f
            val baseProgress = (khatamCount.toFloat() / 114f).coerceIn(0f, 1f)
            maxOf(baseProgress, lastReadFraction.coerceIn(0f, 1f))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.05f
        )
    }

    fun setTab(tab: QuranTab) {
        _selectedTab.value = tab
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleSurahBookmark(surahNumber: Int, current: Boolean) {
        viewModelScope.launch {
            repository.toggleSurahBookmark(surahNumber, current)
        }
    }

    fun toggleSurahKhatam(surahNumber: Int, current: Boolean) {
        viewModelScope.launch {
            repository.toggleSurahKhatam(surahNumber, current)
        }
    }

    fun toggleVerseBookmark(verseId: String, current: Boolean) {
        viewModelScope.launch {
            repository.toggleVerseBookmark(verseId, current)
        }
    }
}
