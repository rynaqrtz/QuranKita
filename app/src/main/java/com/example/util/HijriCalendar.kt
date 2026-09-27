package com.example.util

data class HijriDate(
    val year: Int,
    val month: Int,
    val day: Int
)

data class GregorianDate(
    val year: Int,
    val month: Int,
    val day: Int
)

object HijriCalendar {

    private const val EPOCH = 1948439

    val MONTH_NAMES = listOf(
        "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'uts Tsani",
        "Jumadil Ula", "Jumadil Akhir", "Rajab", "Sya'ban",
        "Ramadhan", "Syawwal", "Dzulqa'dah", "Dzulhijjah"
    )

    fun isLeapYear(year: Int): Boolean = (11 * year + 13) % 30 < 11

    private fun monthLength(year: Int, month: Int): Int = when {
        month % 2 == 1 -> 30
        month == 12 && isLeapYear(year) -> 30
        else -> 29
    }

    private fun daysBeforeYear(year: Int): Int {
        var leaps = 0
        for (y in 1 until year) {
            if (isLeapYear(y)) leaps++
        }
        return (year - 1) * 354 + leaps
    }

    private fun toJulianDay(year: Int, month: Int, day: Int): Int {
        val a = (14 - month) / 12
        val yy = year + 4800 - a
        val mm = month + 12 * a - 3
        return day + (153 * mm + 2) / 5 + 365 * yy + yy / 4 - yy / 100 + yy / 400 - 32045
    }

    private fun fromJulianDay(jdn: Int): GregorianDate {
        val a = jdn + 32044
        val b = (4 * a + 3) / 146097
        val c = a - (146097 * b) / 4
        val d = (4 * c + 3) / 1461
        val e = c - (1461 * d) / 4
        val mm = (5 * e + 2) / 153
        return GregorianDate(
            year = 100 * b + d - 4800 + mm / 10,
            month = mm + 3 - 12 * (mm / 10),
            day = e - (153 * mm + 2) / 5 + 1
        )
    }

    fun fromGregorian(year: Int, month: Int, day: Int): HijriDate {
        val jdn = toJulianDay(year, month, day)
        var hy = (jdn - EPOCH) / 354 + 1
        while (EPOCH + daysBeforeYear(hy) > jdn) hy--
        while (EPOCH + daysBeforeYear(hy + 1) <= jdn) hy++

        var remaining = jdn - (EPOCH + daysBeforeYear(hy))
        var hm = 1
        while (true) {
            val length = monthLength(hy, hm)
            if (remaining < length) break
            remaining -= length
            hm++
        }
        return HijriDate(hy, hm, remaining + 1)
    }

    fun toGregorian(date: HijriDate): GregorianDate {
        var jdn = EPOCH + daysBeforeYear(date.year)
        for (m in 1 until date.month) jdn += monthLength(date.year, m)
        jdn += date.day - 1
        return fromJulianDay(jdn)
    }

    fun format(date: HijriDate): String =
        "${date.day} ${MONTH_NAMES[date.month - 1]} ${date.year} H"
}
