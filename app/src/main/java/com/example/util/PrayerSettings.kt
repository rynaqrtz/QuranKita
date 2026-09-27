package com.example.util

import android.content.Context

object PrayerSettings {

    private const val PREFS = "prayer_settings"
    private const val KEY_CITY = "city_name"

    fun loadCity(context: Context): CityLocation {
        val saved = prefs(context).getString(KEY_CITY, null)
        return PrayerTimeCalculator.DEFAULT_CITIES.firstOrNull { it.name == saved }
            ?: PrayerTimeCalculator.DEFAULT_CITIES.first()
    }

    fun saveCity(context: Context, city: CityLocation) {
        prefs(context).edit().putString(KEY_CITY, city.name).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
