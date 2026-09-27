package com.example

import com.example.data.local.entity.SurahEntity
import com.example.data.local.entity.SurahFtsEntity
import com.example.data.local.entity.VerseEntity
import com.example.data.local.entity.VerseFtsEntity
import com.example.data.model.AsmaulHusnaItem
import com.example.data.model.JuzInfo
import com.example.data.model.Qari
import com.example.data.model.QuranDua
import com.example.util.HijriCalendar
import com.example.util.HijriDate
import com.example.util.PrayerTimeCalculator
import com.example.util.TajwidFormatter
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class QuranKitaUnitTest {

    @Test
    fun prayerTimes_calculation_returnsAllFivePrayers() {
        val times = PrayerTimeCalculator.calculatePrayerTimes(
            latitude = -6.2088,
            longitude = 106.8456,
            timeZoneOffset = 7.0,
            date = Date()
        )

        assertEquals(6, times.size)
        val names = times.map { it.name }
        assertTrue(names.contains("Subuh"))
        assertTrue(names.contains("Dzuhur"))
        assertTrue(names.contains("Ashar"))
        assertTrue(names.contains("Maghrib"))
        assertTrue(names.contains("Isya"))
    }

    @Test
    fun tajwidFormatter_formatsProperly() {
        val arabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
        val withTajwid = TajwidFormatter.formatWithTajwid(arabic, isTajwidEnabled = true)
        val withoutTajwid = TajwidFormatter.formatWithTajwid(arabic, isTajwidEnabled = false)

        assertEquals(arabic, withTajwid.text)
        assertEquals(arabic, withoutTajwid.text)
    }

    @Test
    fun ftsEntities_mappingCorrectly() {
        val surah = SurahEntity(
            number = 1,
            nameLatin = "Al-Fatihah",
            nameArabic = "الفاتحة",
            meaning = "Pembukaan",
            verseCount = 7,
            revelation = "Mekah"
        )
        val surahFts = SurahFtsEntity(
            rowid = surah.number,
            number = surah.number,
            nameLatin = surah.nameLatin,
            nameArabic = surah.nameArabic,
            meaning = surah.meaning
        )
        assertEquals(1, surahFts.rowid)
        assertEquals("Al-Fatihah", surahFts.nameLatin)

        val verse = VerseEntity(
            id = "1_1",
            surahNumber = 1,
            verseNumber = 1,
            arabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            transliteration = "Bismillah",
            translation = "Dengan nama Allah",
            tafsir = "Basmalah"
        )
        val verseFts = VerseFtsEntity(
            rowid = 1001,
            surahNumber = verse.surahNumber,
            verseNumber = verse.verseNumber,
            arabic = verse.arabic,
            transliteration = verse.transliteration,
            translation = verse.translation,
            tafsir = verse.tafsir
        )
        assertEquals(1001, verseFts.rowid)
        assertEquals("Dengan nama Allah", verseFts.translation)
    }

    @Test
    fun juzInfo_coversAllThirtyJuz() {
        val allJuz = JuzInfo.ALL_JUZ
        assertEquals(30, allJuz.size)
        assertEquals(1, allJuz.first().number)
        assertEquals(30, allJuz.last().number)
        assertEquals(1, allJuz.first().startSurahNumber)
        assertEquals(114, allJuz.last().endSurahNumber)
    }

    @Test
    fun quranDua_containsEssentialCategories() {
        val allDuas = QuranDua.ALL_DUAS
        assertTrue(allDuas.isNotEmpty())
        assertTrue(allDuas.any { it.category == "Rabbana" })
        assertTrue(allDuas.any { it.category == "Para Nabi" })
        assertTrue(allDuas.all { it.arabic.isNotBlank() && it.translation.isNotBlank() })
    }

    @Test
    fun asmaulHusna_containsValidNames() {
        val names = AsmaulHusnaItem.ALL_NAMES
        assertTrue(names.isNotEmpty())
        assertEquals("Ar-Rahman", names.first().latin)
        assertTrue(names.all { it.number > 0 && it.arabic.isNotBlank() && it.meaning.isNotBlank() })
    }

    @Test
    fun qaris_hasAlafasyAsDefault() {
        val qaris = Qari.ALL_QARIS
        assertEquals(4, qaris.size)
        assertEquals("misyari", qaris.first().id)
    }

    @Test
    fun islamicEvents_containsRamadanAndMajorEvents() {
        val events = com.example.data.model.IslamicEvent.getUpcomingEvents()
        assertTrue(events.isNotEmpty())
        assertTrue(events.any { it.id == "ramadan_1448" })
        assertTrue(events.any { it.id == "idul_fitri_1448" })
        assertTrue(events.any { it.id == "idul_adha_1448" })
        assertTrue(events.all { it.title.isNotBlank() && it.hijriDate.isNotBlank() })
    }

    @Test
    fun hijriCalendar_matchesIndonesianAnnouncements() {
        assertEquals(HijriDate(1446, 10, 1), HijriCalendar.fromGregorian(2025, 3, 30))
        assertEquals(HijriDate(1446, 12, 10), HijriCalendar.fromGregorian(2025, 6, 6))
        assertEquals(HijriDate(1447, 1, 1), HijriCalendar.fromGregorian(2025, 6, 26))
        assertEquals(HijriDate(1448, 1, 1), HijriCalendar.fromGregorian(2026, 6, 16))
        assertEquals(HijriDate(1448, 4, 15), HijriCalendar.fromGregorian(2026, 9, 27))
    }

    @Test
    fun hijriCalendar_roundTripsGregorian() {
        val samples = listOf(
            Triple(2026, 1, 1),
            Triple(2026, 9, 27),
            Triple(2027, 6, 6),
            Triple(2030, 12, 31)
        )
        for ((y, m, d) in samples) {
            val back = HijriCalendar.toGregorian(HijriCalendar.fromGregorian(y, m, d))
            assertEquals("roundtrip for $y-$m-$d", Triple(y, m, d), Triple(back.year, back.month, back.day))
        }
        val eid = HijriCalendar.toGregorian(HijriDate(1448, 10, 1))
        assertEquals(2027, eid.year)
        assertEquals(3, eid.month)
        assertEquals(9, eid.day)
    }

    @Test
    fun hijriCalendar_hasTwelveNamedMonths() {
        assertEquals(12, HijriCalendar.MONTH_NAMES.size)
        assertEquals("Muharram", HijriCalendar.MONTH_NAMES.first())
        assertEquals("Dzulhijjah", HijriCalendar.MONTH_NAMES.last())
        assertTrue(HijriCalendar.isLeapYear(1447))
        assertFalse(HijriCalendar.isLeapYear(1448))
    }
}
