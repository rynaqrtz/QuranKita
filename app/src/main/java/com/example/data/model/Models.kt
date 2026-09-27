package com.example.data.model
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class Surah(
    val number: Int,
    val nameLatin: String,
    val nameArabic: String,
    val meaning: String,
    val verseCount: Int,
    val revelation: String,
    val isBookmarked: Boolean = false,
    val isKhatam: Boolean = false,
    val memorizedVersesCount: Int = 0
)

@Immutable
data class Verse(
    val id: String, // e.g. "1_1"
    val surahNumber: Int,
    val verseNumber: Int,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val tafsir: String = "",
    val isBookmarked: Boolean = false,
    val isMemorized: Boolean = false
)

@Immutable
data class LastRead(
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val timestamp: Long
)

@Immutable
data class JuzInfo(
    val number: Int,
    val nameArabic: String,
    val nameLatin: String,
    val startSurahNumber: Int,
    val startSurahName: String,
    val startVerse: Int,
    val endSurahNumber: Int,
    val endSurahName: String,
    val endVerse: Int,
    val totalVerses: Int
) {
    companion object {
        val ALL_JUZ = listOf(
            JuzInfo(1, "الجزء الأول", "Al-Fatihah", 1, "Al-Fatihah", 1, 2, "Al-Baqarah", 141, 148),
            JuzInfo(2, "الجزء الثاني", "Sayaqūlu", 2, "Al-Baqarah", 142, 2, "Al-Baqarah", 252, 111),
            JuzInfo(3, "الجزء الثالث", "Tilkar-Rusul", 2, "Al-Baqarah", 253, 3, "Ali 'Imran", 92, 126),
            JuzInfo(4, "الجزء الرابع", "Lan Tanalu", 3, "Ali 'Imran", 93, 4, "An-Nisa'", 23, 131),
            JuzInfo(5, "الجزء الخامس", "Wal-Muhsanat", 4, "An-Nisa'", 24, 4, "An-Nisa'", 147, 124),
            JuzInfo(6, "الجزء السادس", "La Yuhibbullah", 4, "An-Nisa'", 148, 5, "Al-Ma'idah", 81, 110),
            JuzInfo(7, "الجزء السابع", "Wa Iza Sami'u", 5, "Al-Ma'idah", 82, 6, "Al-An'am", 110, 149),
            JuzInfo(8, "الجزء الثامن", "Wa Lau Annana", 6, "Al-An'am", 111, 7, "Al-A'raf", 87, 142),
            JuzInfo(9, "الجزء التاسع", "Qalal Mala'u", 7, "Al-A'raf", 88, 8, "Al-Anfal", 40, 159),
            JuzInfo(10, "الجزء العاشر", "Wa'lamu", 8, "Al-Anfal", 41, 9, "At-Taubah", 92, 127),
            JuzInfo(11, "الجزء الحادي عشر", "Ya'taziruna", 9, "At-Taubah", 93, 11, "Hud", 5, 151),
            JuzInfo(12, "الجزء الثاني عشر", "Wa Ma Min Dabbah", 11, "Hud", 6, 12, "Yusuf", 52, 170),
            JuzInfo(13, "الجزء الثالث عشر", "Wa Ma Ubari'u", 12, "Yusuf", 53, 14, "Ibrahim", 52, 154),
            JuzInfo(14, "الجزء الرابع عشر", "Rubama", 15, "Al-Hijr", 1, 16, "An-Nahl", 128, 227),
            JuzInfo(15, "الجزء الخامس عشر", "Subhanallazi", 17, "Al-Isra'", 1, 18, "Al-Kahf", 74, 185),
            JuzInfo(16, "الجزء السادس عشر", "Qala Alam", 18, "Al-Kahf", 75, 20, "Ta Ha", 135, 269),
            JuzInfo(17, "الجزء السابع عشر", "Iqtaraba", 21, "Al-Anbiya'", 1, 22, "Al-Hajj", 78, 190),
            JuzInfo(18, "الجزء الثامن عشر", "Qad Aflaha", 23, "Al-Mu'minun", 1, 25, "Al-Furqan", 20, 202),
            JuzInfo(19, "الجزء التاسع عشر", "Wa Qalal-Ladhina", 25, "Al-Furqan", 21, 27, "An-Naml", 55, 339),
            JuzInfo(20, "الجزء العشرون", "Amman Khalaqa", 27, "An-Naml", 56, 29, "Al-'Ankabut", 45, 171),
            JuzInfo(21, "الجزء الحادي والعشرون", "Utlu Ma Uhiya", 29, "Al-'Ankabut", 46, 33, "Al-Ahzab", 30, 178),
            JuzInfo(22, "الجزء الثاني والعشرون", "Wa Man Yaqnut", 33, "Al-Ahzab", 31, 36, "Ya Sin", 27, 169),
            JuzInfo(23, "الجزء الثالث والعشرون", "Wa Maliya", 36, "Ya Sin", 28, 39, "Az-Zumar", 31, 357),
            JuzInfo(24, "الجزء الرابع والعشرون", "Fa Man Azlamu", 39, "Az-Zumar", 32, 41, "Fussilat", 46, 175),
            JuzInfo(25, "الجزء الخامس والعشرون", "Ilayhi Yuraddu", 41, "Fussilat", 47, 45, "Al-Jasiyah", 37, 246),
            JuzInfo(26, "الجزء السادس والعشرون", "Ha Mim", 46, "Al-Ahqaf", 1, 51, "Az-Zariyat", 30, 195),
            JuzInfo(27, "الجزء السابع والعشرون", "Qala Fa Ma Khatbukum", 51, "Az-Zariyat", 31, 57, "Al-Hadid", 29, 399),
            JuzInfo(28, "الجزء الثامن والعشرون", "Qad Sami'allah", 58, "Al-Mujadilah", 1, 66, "At-Tahrim", 12, 137),
            JuzInfo(29, "الجزء التاسع والعشرون", "Tabarakallazi", 67, "Al-Mulk", 1, 77, "Al-Mursalat", 50, 431),
            JuzInfo(30, "الجزء الثلاثون", "'Amma Yatasa'alun", 78, "An-Naba'", 1, 114, "An-Nas", 6, 564)
        )
    }
}

@Immutable
data class PrayerTimeInfo(
    val name: String,
    val time: String,
    val isNext: Boolean = false,
    val isCurrent: Boolean = false,
    val remainingTimeText: String = ""
)

@Immutable
data class Qari(
    val id: String,
    val name: String,
    val style: String,
    val subfolder: String
) {
    companion object {
        val ALL_QARIS = listOf(
            Qari(
                id = "misyari",
                name = "Misyari Rasyid Al-Afasy",
                style = "Tilawah Populer & Merdu (Default)",
                subfolder = "Alafasy_128kbps"
            ),
            Qari(
                id = "sudais",
                name = "Abdurrahman As-Sudais",
                style = "Imam Masjidil Haram (Tempo Tegas)",
                subfolder = "Abdurrahmaan_As-Sudais_192kbps"
            ),
            Qari(
                id = "husary",
                name = "Mahmud Khalil Al-Husary",
                style = "Standar Tahfidz (Artikulasi Tajwid Presisi)",
                subfolder = "Husary_128kbps"
            ),
            Qari(
                id = "ghamidi",
                name = "Saad Al-Ghamidi",
                style = "Alunan Lembut & Khusyuk",
                subfolder = "Ghamadi_40kbps"
            )
        )
    }
}

@Immutable
data class DzikirItem(
    val id: Int,
    val title: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val countTarget: Int,
    val currentCount: Int = 0,
    val benefit: String = "",
    val category: String = "Pagi" // Pagi, Petang, Sholat
)

@Immutable
data class QuranDua(
    val id: Int,
    val title: String,
    val surahReference: String,
    val arabic: String,
    val latin: String,
    val translation: String,
    val category: String = "Rabbana" // Rabbana, Para Nabi, Perlindungan, Rezeki, Taubat
) {
    companion object {
        val ALL_DUAS = listOf(
            QuranDua(
                id = 1,
                title = "Doa Kebaikan Dunia & Akhirat (Sapu Jagad)",
                surahReference = "QS. Al-Baqarah [2]: 201",
                arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                latin = "Rabbana atina fid-dunya hasanah wa fil-akhirati hasanah wa qina 'adzaban-nar.",
                translation = "Ya Tuhan kami, berilah kami kebaikan di dunia dan kebaikan di akhirat dan lindungilah kami dari azab neraka.",
                category = "Rabbana"
            ),
            QuranDua(
                id = 2,
                title = "Doa Ketetapan Iman & Hidayah Hati",
                surahReference = "QS. Ali 'Imran [3]: 8",
                arabic = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ",
                latin = "Rabbana la tuzigh qulubana ba'da idz hadaytana wa hab lana mil-ladunka rahmah, innaka antal-wahhab.",
                translation = "Ya Tuhan kami, janganlah Engkau jadikan hati kami condong kepada kesesatan sesudah Engkau beri petunjuk kepada kami, dan karuniakanlah kepada kami rahmat dari sisi-Mu.",
                category = "Rabbana"
            ),
            QuranDua(
                id = 3,
                title = "Doa Pengampunan & Rahmat Orang Tua",
                surahReference = "QS. Al-Isra' [17]: 24",
                arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
                latin = "Rabbir-hamhuma kama rabbayani shaghira.",
                translation = "Wahai Tuhanku, kasihilah mereka keduanya, sebagaimana mereka berdua telah mendidik aku waktu kecil.",
                category = "Para Nabi"
            ),
            QuranDua(
                id = 4,
                title = "Doa Kelapangan Hati & Kemudahan Urusan (Nabi Musa AS)",
                surahReference = "QS. Ta Ha [20]: 25-28",
                arabic = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي يَفْقَهُوا قَوْلِي",
                latin = "Rabbisy-rah li shadri, wa yassir li amri, wahlul 'uqdatam-mil-lisani yafqahu qawli.",
                translation = "Ya Tuhanku, lapangkanlah untukku dadaku, dan mudahkanlah untukku urusanku, dan lepaskanlah kekakuan dari lidahku, supaya mereka mengerti perkataanku.",
                category = "Para Nabi"
            ),
            QuranDua(
                id = 5,
                title = "Doa Pengakuan Dosa & Keselamatan (Nabi Yunus AS)",
                surahReference = "QS. Al-Anbiya' [21]: 87",
                arabic = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
                latin = "La ilaha illa anta subhanaka inni kuntu minazh-zhalimin.",
                translation = "Tidak ada Tuhan selain Engkau. Maha Suci Engkau, sesungguhnya aku adalah termasuk orang-orang yang zalim.",
                category = "Taubat"
            ),
            QuranDua(
                id = 6,
                title = "Doa Keluarga Sakinah & Keturunan Penyejuk Hati",
                surahReference = "QS. Al-Furqan [25]: 74",
                arabic = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
                latin = "Rabbana hab lana min azwajina wa dzurriyyatina qurrata a'yunin waj'alna lil-muttaqina imama.",
                translation = "Ya Tuhan kami, anugerahkanlah kepada kami istri-istri kami dan keturunan kami sebagai penyenang hati (kami), dan jadikanlah kami imam bagi orang-orang yang bertakwa.",
                category = "Rabbana"
            ),
            QuranDua(
                id = 7,
                title = "Doa Kesabaran & Keteguhan Menghadapi Musuh",
                surahReference = "QS. Al-Baqarah [2]: 250",
                arabic = "رَبَّنَا أَفْرِغْ عَلَيْنَا صَبْرًا وَثَبِّتْ أَقْدَامَنَا وَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
                latin = "Rabbana afrigh 'alayna shabraw-wa tsabbit aqdamana wansurna 'alal-qawmil-kafirin.",
                translation = "Ya Tuhan kami, limpahkanlah kesabaran kepada kami, kukuhkanlah langkah kami dan tolonglah kami menghadapi kaum yang kafir.",
                category = "Perlindungan"
            ),
            QuranDua(
                id = 8,
                title = "Doa Memohon Tambahan Ilmu Pengetahuan",
                surahReference = "QS. Ta Ha [20]: 114",
                arabic = "رَّبِّ زِدْنِي عِلْمًا",
                latin = "Rabbi zidni 'ilma.",
                translation = "Ya Tuhanku, tambahkanlah kepadaku ilmu pengetahuan.",
                category = "Para Nabi"
            ),
            QuranDua(
                id = 9,
                title = "Doa Memohon Tempat Tinggal & Rezeki Penuh Berkah (Nabi Nuh AS)",
                surahReference = "QS. Al-Mu'minun [23]: 29",
                arabic = "رَّبِّ أَنزِلْنِي مُنزَلًا مُّبَارَكًا وَأَنتَ خَيْرُ الْمُنزِلِينَ",
                latin = "Rabbi anzilni munzalam-mubarakanw-wa anta khayrul-munzilin.",
                translation = "Ya Tuhanku, tempatkanlah aku pada tempat yang diberkahi, dan Engkau adalah sebaik-baik pemberi tempat.",
                category = "Rezeki"
            ),
            QuranDua(
                id = 10,
                title = "Doa Taubat & Pengampunan Dosa (Nabi Adam AS)",
                surahReference = "QS. Al-A'raf [7]: 23",
                arabic = "رَبَّنَا ظَلَمْنَا أَنفُسَنَا وَإِن لَّمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
                latin = "Rabbana zhalamna anfusana wa illam taghfir lana wa tarhamna lanakunanna minal-khasirin.",
                translation = "Ya Tuhan kami, kami telah menzalimi diri kami sendiri. Jika Engkau tidak mengampuni kami dan memberi rahmat kepada kami, niscaya pastilah kami termasuk orang-orang yang rugi.",
                category = "Taubat"
            ),
            QuranDua(
                id = 11,
                title = "Doa Perlindungan dari Godaan Syaitan",
                surahReference = "QS. Al-Mu'minun [23]: 97-98",
                arabic = "رَّبِّ أَعُوذُ بِكَ مِنْ هَمَزَاتِ الشَّيَاطِينِ وَأَعُوذُ بِكَ رَبِّ أَن يَحْضُرُونِ",
                latin = "Rabbi a'udzu bika min hamazatisy-syayathini wa a'udzu bika rabbi ay-yahdhurun.",
                translation = "Ya Tuhanku, aku berlindung kepada Engkau dari bisikan-bisikan syaitan, dan aku berlindung pula kepada Engkau ya Tuhanku, agar mereka tidak mendekati aku.",
                category = "Perlindungan"
            ),
            QuranDua(
                id = 12,
                title = "Doa Keturunan yang Saleh (Nabi Zakariya AS)",
                surahReference = "QS. Ali 'Imran [3]: 38",
                arabic = "رَبِّ هَبْ لِي مِن لَّدُنكَ ذُرِّيَّةً طَيِّبَةً ۖ إِنَّكَ سَمِيعُ الدُّعَاءِ",
                latin = "Rabbi hab li mil-ladunka dzurriyyatan thayyibah, innaka sami'ud-du'a'.",
                translation = "Ya Tuhanku, berilah aku keturunan yang baik dari sisi-Mu. Sesungguhnya Engkau Maha Mendengar doa.",
                category = "Para Nabi"
            )
        )
    }
}

@Immutable
data class AsmaulHusnaItem(
    val number: Int,
    val arabic: String,
    val latin: String,
    val translation: String,
    val meaning: String
) {
    companion object {
        val ALL_NAMES = listOf(
            AsmaulHusnaItem(1, "الرَّحْمَنُ", "Ar-Rahman", "Maha Pengasih", "Mencurahkan kasih sayang luas kepada seluruh makhluk"),
            AsmaulHusnaItem(2, "الرَّحِيمُ", "Ar-Rahim", "Maha Penyayang", "Memberi rahmat khusus kepada hamba yang beriman"),
            AsmaulHusnaItem(3, "الْمَلِكُ", "Al-Malik", "Maha Raja", "Pemilik mutlak segala kekuasaan alam semesta"),
            AsmaulHusnaItem(4, "الْقُدُّوسُ", "Al-Quddus", "Maha Suci", "Suci dari segala kekurangan dan cela"),
            AsmaulHusnaItem(5, "السَّلاَمُ", "As-Salam", "Maha Sejahtera", "Maha Memberi kedamaian dan keselamatan"),
            AsmaulHusnaItem(6, "الْمُؤْمِنُ", "Al-Mu'min", "Maha Pemberi Keamanan", "Memberikan ketenangan dan mengabulkan keamanan"),
            AsmaulHusnaItem(7, "الْمُهَيْمِنُ", "Al-Muhaymin", "Maha Memelihara", "Maha Mengawasi dan memelihara segala urusan"),
            AsmaulHusnaItem(8, "الْعَزِيزُ", "Al-'Aziz", "Maha Perkasa", "Maha Mulia dan tiada yang mampu menandingi-Nya"),
            AsmaulHusnaItem(9, "الْجَبَّارُ", "Al-Jabbar", "Maha Memaksa", "Maha Kuasa kehendak-Nya terlaksana sempurna"),
            AsmaulHusnaItem(10, "الْمُتَكَبِّرُ", "Al-Mutakabbir", "Maha Megah", "Pemilik segala keagungan hakiki"),
            AsmaulHusnaItem(11, "الْخَالِقُ", "Al-Khaliq", "Maha Pencipta", "Menciptakan segala sesuatu dari ketiadaan"),
            AsmaulHusnaItem(12, "الْبَارِئُ", "Al-Bari'", "Maha Pembuat", "Mengadakan ciptaan secara serasi dan seimbang")
        )
    }
}

@Stable
data class QuizQuestion(
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val promptArabic: String,
    val promptTranslation: String,
    val optionsArabic: List<String>,
    val correctIndex: Int,
    val explanation: String = "Lanjutan ayat ini termaktub dalam Al-Qur'anul Karim."
)
