package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object QuranList : Screen("quran_list")
    object SurahReader : Screen("surah_reader/{surahNumber}") {
        fun createRoute(surahNumber: Int) = "surah_reader/$surahNumber"
    }
    object Tahfidz : Screen("tahfidz")
    object PrayerQibla : Screen("prayer_qibla")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Dzikir : Screen("dzikir")
    object Dua : Screen("dua")
    object AsmaulHusna : Screen("asmaul_husna")
    object QuranQuiz : Screen("quran_quiz")
    object KhatamTracker : Screen("khatam_tracker")
}
