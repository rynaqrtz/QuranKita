package com.example.ui.prayer

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.PrayerTimeInfo
import com.example.util.CityLocation
import com.example.util.CompassState
import com.example.util.PrayerSettings
import com.example.util.PrayerTimeCalculator
import com.example.util.QiblaCompassHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

enum class PrayerQiblaTab {
    SHOLAT,
    KIBLAT
}

class PrayerQiblaViewModel(application: Application) : AndroidViewModel(application) {

    private val compassHelper = QiblaCompassHelper(application)
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _selectedTab = MutableStateFlow(PrayerQiblaTab.SHOLAT)
    val selectedTab: StateFlow<PrayerQiblaTab> = _selectedTab.asStateFlow()

    private val _selectedCity = MutableStateFlow(PrayerSettings.loadCity(application))
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _prayerTimes = MutableStateFlow<List<PrayerTimeInfo>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTimeInfo>> = _prayerTimes.asStateFlow()

    val compassState: StateFlow<CompassState> = compassHelper.compassState

    private var hasVibratedOnAligned = false

    init {
        updatePrayerTimes()
        compassHelper.setLocation(_selectedCity.value.latitude, _selectedCity.value.longitude)
    }

    fun setTab(tab: PrayerQiblaTab) {
        _selectedTab.value = tab
        if (tab == PrayerQiblaTab.KIBLAT) {
            compassHelper.startListening()
        } else {
            compassHelper.stopListening()
        }
    }

    fun selectCity(city: CityLocation) {
        _selectedCity.value = city
        PrayerSettings.saveCity(getApplication(), city)
        compassHelper.setLocation(city.latitude, city.longitude)
        updatePrayerTimes()
    }

    private fun updatePrayerTimes() {
        val city = _selectedCity.value
        val times = PrayerTimeCalculator.calculatePrayerTimes(
            latitude = city.latitude,
            longitude = city.longitude,
            timeZoneOffset = city.timeZoneOffset,
            date = Date()
        )
        _prayerTimes.value = times
    }

    fun onResume() {
        if (_selectedTab.value == PrayerQiblaTab.KIBLAT) {
            compassHelper.startListening()
        }
        updatePrayerTimes()
    }

    fun onPause() {
        compassHelper.stopListening()
    }

    fun checkAndTriggerHaptic(isAligned: Boolean) {
        if (isAligned && !hasVibratedOnAligned) {
            hasVibratedOnAligned = true
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(50L)
                }
            } catch (_: Exception) {
                // Ignore vibration failure
            }
        } else if (!isAligned) {
            hasVibratedOnAligned = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        compassHelper.stopListening()
    }
}
