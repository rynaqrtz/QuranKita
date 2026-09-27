package com.example.util

import com.example.data.model.PrayerTimeInfo
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class CityLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneOffset: Double
)

object PrayerTimeCalculator {

    val DEFAULT_CITIES = listOf(
        CityLocation("Jakarta", -6.2088, 106.8456, 7.0),
        CityLocation("Surabaya", -7.2575, 112.7521, 7.0),
        CityLocation("Bandung", -6.9175, 107.6191, 7.0),
        CityLocation("Medan", 3.5952, 98.6722, 7.0),
        CityLocation("Makassar", -5.1477, 119.4327, 8.0),
        CityLocation("Semarang", -6.9667, 110.4167, 7.0),
        CityLocation("Yogyakarta", -7.7956, 110.3695, 7.0),
        CityLocation("Banda Aceh", 5.5483, 95.3238, 7.0),
        CityLocation("Balikpapan", -1.2379, 116.8529, 8.0),
        CityLocation("Denpasar", -8.6705, 115.2126, 8.0),
        CityLocation("Jayapura", -2.5916, 140.6690, 9.0)
    )

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        timeZoneOffset: Double = 7.0,
        date: Date = Date()
    ): List<PrayerTimeInfo> {
        val calendar = Calendar.getInstance().apply { time = date }
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        // Fractional year (radians)
        val gamma = 2.0 * Math.PI / 365.0 * (dayOfYear - 1)

        // Equation of Time (minutes)
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) -
                0.014615 * cos(2 * gamma) - 0.040849 * sin(2 * gamma))

        // Solar Declination (radians)
        val decl = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) -
                0.006758 * cos(2 * gamma) + 0.000907 * sin(2 * gamma) -
                0.002697 * cos(3 * gamma) + 0.00148 * sin(3 * gamma)

        val latRad = Math.toRadians(latitude)

        // Solar Noon (hours)
        val timeOffset = eqTime + 4.0 * longitude - 60.0 * timeZoneOffset
        val solarNoon = (720.0 - timeOffset) / 60.0

        // Kemenag angles: Fajr 20.0 deg, Isha 18.0 deg, ikhtiyat +2 minutes
        fun hourAngle(zenithDeg: Double): Double {
            val zenithRad = Math.toRadians(zenithDeg)
            val cosHa = (cos(zenithRad) - sin(latRad) * sin(decl)) / (cos(latRad) * cos(decl))
            val clamped = cosHa.coerceIn(-1.0, 1.0)
            return Math.toDegrees(acos(clamped)) / 15.0
        }

        // Sunrise & Sunset (Zenith 90.833 deg)
        val haSunrise = hourAngle(90.833)
        val sunrise = solarNoon - haSunrise
        val sunset = solarNoon + haSunrise

        // Fajr (Zenith 90 + 20 = 110 deg)
        val haFajr = hourAngle(110.0)
        val fajr = solarNoon - haFajr

        // Asr (Shafi'i: shadow length = object + shadow at noon)
        val noonAngle = abs(latitude - Math.toDegrees(decl))
        val noonShadow = tan(Math.toRadians(noonAngle))
        val asrAngle = Math.toDegrees(atan2(1.0, 1.0 + noonShadow))
        val haAsr = hourAngle(90.0 - asrAngle)
        val asr = solarNoon + haAsr

        // Isha (Zenith 90 + 18 = 108 deg)
        val haIsha = hourAngle(108.0)
        val isha = solarNoon + haIsha

        // Ikhtiyat addition (+2 min = 2/60 hour)
        val ikhtiyat = 2.0 / 60.0

        fun formatHours(h: Double): String {
            val totalMinutes = floor((h + ikhtiyat) * 60.0).toInt()
            val hour = (totalMinutes / 60) % 24
            val min = totalMinutes % 60
            return String.format(Locale.getDefault(), "%02d:%02d", hour, min)
        }

        // Convert current time to float hour
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60.0

        val timesRaw = listOf(
            "Subuh" to (fajr + ikhtiyat),
            "Terbit" to (sunrise + ikhtiyat),
            "Dzuhur" to (solarNoon + ikhtiyat),
            "Ashar" to (asr + ikhtiyat),
            "Maghrib" to (sunset + ikhtiyat),
            "Isya" to (isha + ikhtiyat)
        )

        // Find next prayer
        var nextFound = false
        val result = mutableListOf<PrayerTimeInfo>()

        for ((name, hourVal) in timesRaw) {
            val isNext = if (!nextFound && hourVal > currentHour && name != "Terbit") {
                nextFound = true
                true
            } else false

            val remainingMin = if (isNext) {
                ((hourVal - currentHour) * 60).toInt()
            } else 0

            val remainingText = if (isNext) {
                val hRem = remainingMin / 60
                val mRem = remainingMin % 60
                if (hRem > 0) "$hRem jam $mRem mnt lagi" else "$mRem menit lagi"
            } else ""

            result.add(
                PrayerTimeInfo(
                    name = name,
                    time = formatHours(hourVal - ikhtiyat),
                    isNext = isNext,
                    remainingTimeText = remainingText
                )
            )
        }

        // If all prayers passed today, Subuh tomorrow is next
        if (!nextFound) {
            val first = result.firstOrNull { it.name == "Subuh" }
            if (first != null) {
                val idx = result.indexOf(first)
                result[idx] = first.copy(isNext = true, remainingTimeText = "Besok subuh")
            }
        }

        return result
    }

    fun getTodayHijriDate(): String {
        val cal = Calendar.getInstance()
        return HijriCalendar.format(
            HijriCalendar.fromGregorian(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1,
                day = cal.get(Calendar.DAY_OF_MONTH)
            )
        )
    }
}
