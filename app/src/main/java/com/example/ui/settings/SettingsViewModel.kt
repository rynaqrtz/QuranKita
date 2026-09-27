package com.example.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.DownloadedAudioEntity
import com.example.data.model.Qari
import com.example.data.repository.QuranRepository
import com.example.util.QuranReminderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository
    private val prefs = application.getSharedPreferences("quran_settings_prefs", Context.MODE_PRIVATE)

    private val _selectedQari = MutableStateFlow(Qari.ALL_QARIS.first())
    val selectedQari: StateFlow<Qari> = _selectedQari.asStateFlow()

    private val _arabicFontSize = MutableStateFlow(prefs.getFloat("arabic_font_size", 26f))
    val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

    private val _translationFontSize = MutableStateFlow(prefs.getFloat("translation_font_size", 15f))
    val translationFontSize: StateFlow<Float> = _translationFontSize.asStateFlow()

    private val _isTajweedHighlighterEnabled = MutableStateFlow(prefs.getBoolean("tajweed_highlighter_enabled", true))
    val isTajweedHighlighterEnabled: StateFlow<Boolean> = _isTajweedHighlighterEnabled.asStateFlow()

    private val _isDailyReminderEnabled = MutableStateFlow(QuranReminderManager.isReminderEnabled(application))
    val isDailyReminderEnabled: StateFlow<Boolean> = _isDailyReminderEnabled.asStateFlow()

    private val _reminderHour = MutableStateFlow(QuranReminderManager.getReminderHour(application))
    val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

    private val _reminderMinute = MutableStateFlow(QuranReminderManager.getReminderMinute(application))
    val reminderMinute: StateFlow<Int> = _reminderMinute.asStateFlow()

    private val _khatamTargetDays = MutableStateFlow(prefs.getInt("khatam_target_days", 30))
    val khatamTargetDays: StateFlow<Int> = _khatamTargetDays.asStateFlow()

    val downloadedAudios: StateFlow<List<DownloadedAudioEntity>>

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuranRepository(db.quranDao())

        downloadedAudios = repository.downloadedAudios.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun selectQari(qari: Qari) {
        _selectedQari.value = qari
    }

    fun setArabicFontSize(size: Float) {
        _arabicFontSize.value = size
        prefs.edit().putFloat("arabic_font_size", size).apply()
    }

    fun setTranslationFontSize(size: Float) {
        _translationFontSize.value = size
        prefs.edit().putFloat("translation_font_size", size).apply()
    }

    fun toggleTajweedHighlighter(enabled: Boolean) {
        _isTajweedHighlighterEnabled.value = enabled
        prefs.edit().putBoolean("tajweed_highlighter_enabled", enabled).apply()
    }

    fun toggleDailyReminder(enabled: Boolean) {
        _isDailyReminderEnabled.value = enabled
        QuranReminderManager.setReminder(
            getApplication(),
            enabled,
            _reminderHour.value,
            _reminderMinute.value
        )
    }

    fun setReminderTime(hour: Int, minute: Int) {
        _reminderHour.value = hour
        _reminderMinute.value = minute
        if (_isDailyReminderEnabled.value) {
            QuranReminderManager.setReminder(
                getApplication(),
                true,
                hour,
                minute
            )
        }
    }

    fun setKhatamTargetDays(days: Int) {
        _khatamTargetDays.value = days
        prefs.edit().putInt("khatam_target_days", days).apply()
    }

    fun deleteDownloadedAudio(audio: DownloadedAudioEntity) {
        viewModelScope.launch {
            repository.deleteDownloadedAudio(audio.key, audio.localFilePath)
        }
    }
}
