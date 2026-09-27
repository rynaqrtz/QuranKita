package com.example.data.model

import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

data class IslamicEvent(
    val id: String,
    val title: String,
    val hijriDate: String,
    val targetGregorianDate: Date,
    val description: String,
    val category: String,
    val recommendedPractices: List<String> = emptyList()
) {
    val daysRemaining: Long
        get() {
            val now = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val target = Calendar.getInstance().apply {
                time = targetGregorianDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val diff = target - now
            return if (diff > 0) TimeUnit.MILLISECONDS.toDays(diff) else 0
        }

    companion object {
        fun getUpcomingEvents(): List<IslamicEvent> {
            val cal = Calendar.getInstance()
            val currentYear = cal.get(Calendar.YEAR)

            val ramadanCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.FEBRUARY)
                set(Calendar.DAY_OF_MONTH, 8)
            }

            val nuzululQuranCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.FEBRUARY)
                set(Calendar.DAY_OF_MONTH, 24)
            }

            val idulFitriCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.MARCH)
                set(Calendar.DAY_OF_MONTH, 10)
            }

            val arafahCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.MAY)
                set(Calendar.DAY_OF_MONTH, 16)
            }

            val idulAdhaCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.MAY)
                set(Calendar.DAY_OF_MONTH, 17)
            }

            val tahunBaruHijriahCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.JUNE)
                set(Calendar.DAY_OF_MONTH, 6)
            }

            val asyuraCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.JUNE)
                set(Calendar.DAY_OF_MONTH, 15)
            }

            val maulidCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2026)
                set(Calendar.MONTH, Calendar.AUGUST)
                set(Calendar.DAY_OF_MONTH, 25)
            }

            val israMirajCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2027)
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 6)
            }

            return listOf(
                IslamicEvent(
                    id = "ramadan_1448",
                    title = "Awal Bulan Suci Ramadhan",
                    hijriDate = "1 Ramadhan 1448 H",
                    targetGregorianDate = ramadanCal.time,
                    description = "Bulan penuh berkah, rahmat, ampunan, dan diturunkannya Al-Qur'anul Karim.",
                    category = "Bulan Mulia",
                    recommendedPractices = listOf(
                        "Puasa Wajib sebulan penuh",
                        "Sholat Tarawih dan Witir berjamaah",
                        "Tadarrus dan Khatam Al-Qur'an",
                        "Memperbanyak Infaq dan Sedekah",
                        "I'tikaf di sepuluh malam terakhir"
                    )
                ),
                IslamicEvent(
                    id = "nuzulul_quran_1448",
                    title = "Malam Nuzulul Qur'an",
                    hijriDate = "17 Ramadhan 1448 H",
                    targetGregorianDate = nuzululQuranCal.time,
                    description = "Peringatan malam pertama kali diturunkannya wahyu Al-Qur'an kepada Nabi Muhammad SAW di Gua Hira.",
                    category = "Peringatan",
                    recommendedPractices = listOf(
                        "Memperbanyak tilawah Al-Qur'an",
                        "Mengkaji tafsir dan kandungan ayat",
                        "Qiyamul Lail dan doa mustajab"
                    )
                ),
                IslamicEvent(
                    id = "idul_fitri_1448",
                    title = "Hari Raya Idul Fitri",
                    hijriDate = "1 Syawal 1448 H",
                    targetGregorianDate = idulFitriCal.time,
                    description = "Hari kemenangan setelah sebulan penuh berpuasa, kembali ke fitrah yang suci.",
                    category = "Hari Raya",
                    recommendedPractices = listOf(
                        "Membayar Zakat Fitrah sebelum sholat Id",
                        "Melaksanakan Sholat Idul Fitri",
                        "Mengumandangkan takbir, tahmid, dan tahlil",
                        "Menyambung tali silaturahim dan saling memaafkan"
                    )
                ),
                IslamicEvent(
                    id = "arafah_1448",
                    title = "Hari Arafah (Puasa Sunnah Arafah)",
                    hijriDate = "9 Dzulhijjah 1448 H",
                    targetGregorianDate = arafahCal.time,
                    description = "Hari puncak ibadah haji (wukuf di Arafah). Puasa pada hari ini menghapus dosa dua tahun (setahun lalu dan setahun mendatang).",
                    category = "Puasa Sunnah",
                    recommendedPractices = listOf(
                        "Puasa Sunnah Arafah bagi yang tidak berhaji",
                        "Memperbanyak doa terbaik di hari Arafah",
                        "Dzikir Tahlil dan Istighfar"
                    )
                ),
                IslamicEvent(
                    id = "idul_adha_1448",
                    title = "Hari Raya Idul Adha & Hari Tasyrik",
                    hijriDate = "10 Dzulhijjah 1448 H",
                    targetGregorianDate = idulAdhaCal.time,
                    description = "Hari raya kurban untuk meneladani keikhlasan dan ketakwaan Nabi Ibrahim AS dan Nabi Ismail AS.",
                    category = "Hari Raya",
                    recommendedPractices = listOf(
                        "Melaksanakan Sholat Idul Adha",
                        "Menyembelih hewan kurban (Udhiyah)",
                        "Takbiran hingga akhir Hari Tasyrik (13 Dzulhijjah)",
                        "Menghindari puasa di hari Tasyrik"
                    )
                ),
                IslamicEvent(
                    id = "tahun_baru_1449",
                    title = "Tahun Baru Islam (1 Muharram)",
                    hijriDate = "1 Muharram 1449 H",
                    targetGregorianDate = tahunBaruHijriahCal.time,
                    description = "Awal tahun kalender Hijriah, mengenang peristiwa hijrah Rasulullah SAW dari Makkah ke Madinah.",
                    category = "Tahun Baru",
                    recommendedPractices = listOf(
                        "Muhasabah dan evaluasi diri",
                        "Membaca doa akhir tahun dan awal tahun",
                        "Memperbanyak amal kebajikan"
                    )
                ),
                IslamicEvent(
                    id = "asyura_1449",
                    title = "Hari Asyura & Tasu'a",
                    hijriDate = "9 & 10 Muharram 1449 H",
                    targetGregorianDate = asyuraCal.time,
                    description = "Hari diselamatkannya Nabi Musa AS dari kejaran Fir'aun. Puasa Asyura menghapus dosa setahun yang lalu.",
                    category = "Puasa Sunnah",
                    recommendedPractices = listOf(
                        "Puasa Sunnah Tasu'a (9 Muharram)",
                        "Puasa Sunnah Asyura (10 Muharram)",
                        "Menyantuni anak yatim dan bersedekah"
                    )
                ),
                IslamicEvent(
                    id = "isra_miraj_1448",
                    title = "Peringatan Isra Mi'raj",
                    hijriDate = "27 Rajab 1448 H",
                    targetGregorianDate = israMirajCal.time,
                    description = "Perjalanan agung Rasulullah SAW dari Masjidil Haram ke Masjidil Aqsa hingga Sidratul Muntaha, menerima perintah sholat 5 waktu.",
                    category = "Peringatan",
                    recommendedPractices = listOf(
                        "Meningkatkan kualitas sholat fardhu",
                        "Meneladani perjalanan suci Rasulullah SAW",
                        "Memperbanyak istighfar di bulan Rajab"
                    )
                )
            )
        }
    }
}
