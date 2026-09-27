package com.example.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val searchResults: StateFlow<List<Verse>>
    val surahResults: StateFlow<List<Surah>>

    val popularKeywords = listOf("Sabar", "Surga", "Sholat", "Rezeki", "Hidayah", "Taubat", "Ilmu")

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuranRepository(db.quranDao())

        searchResults = _query.flatMapLatest { q ->
            if (q.isBlank() || q.length < 2) {
                flowOf(emptyList())
            } else {
                repository.searchVersesFts(q)
                    .catch { emit(emptyList()) }
                    .flatMapLatest { fts ->
                        if (fts.isNotEmpty()) flowOf(fts) else repository.searchVerses(q)
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        surahResults = _query.flatMapLatest { q ->
            if (q.isBlank() || q.length < 2) {
                flowOf(emptyList())
            } else {
                repository.searchSurahsFts(q)
                    .catch { emit(emptyList()) }
                    .flatMapLatest { fts ->
                        if (fts.isNotEmpty()) flowOf(fts) else repository.searchSurahs(q)
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }
}
