# 📖 QuranKita

<div align="center">

<img src="https://cdn.aceimg.com/9ZQ31x8nD.png" alt="QuranKita" width="100%">

![Version](https://img.shields.io/badge/version-1.0-10B981?style=for-the-badge&logo=android&logoColor=white)
![CI](https://img.shields.io/github/actions/workflow/status/rynaqrtz/QuranKita/android-build.yml?branch=main&label=build&style=for-the-badge&logo=githubactions&logoColor=white)
![Min SDK](https://img.shields.io/badge/Android-7.0%20%2B%20(API%2024)-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Tests](https://img.shields.io/badge/tests-17%20passed-22C55E?style=for-the-badge&logoColor=white)
![Size](https://img.shields.io/badge/APK-4%2C3%20MB-10B981?style=for-the-badge&logoColor=white)
![License](https://img.shields.io/badge/license-AGPL--3.0-blue?style=for-the-badge)

**Aplikasi Al-Qur'an & pendamping ibadah harian untuk Android — 6.236 ayat lengkap
terpasang di dalam APK, tanpa iklan, tanpa akun, tanpa telemetri.**

[Fitur](#-fitur) • [Unduh](#-unduh) • [Performa](#-performa) • [Bangun sendiri](#-bangun-sendiri) • [Struktur](#-struktur-proyek) • [Lisensi](#-lisensi)

</div>

---

## 🌟 Yang membuatnya berbeda

Kebanyakan aplikasi Al-Qur'an mengunduh teks, terjemahan, dan tafsir dari server saat
pertama dibuka. QuranKita **membundel semuanya ke dalam APK** — 5,9 MB data ayat
yang terbaca *streaming*, tanpa satu pun permintaan jaringan untuk membacanya.

| | Isi | Sumber |
| :--- | :--- | :--- |
| 📜 Teks Arab | 6.236 ayat, rasm Utsmani | `quran-uthmani` (Tanzil/alquran.cloud) |
| 🇮🇩 Terjemahan | Bahasa Indonesia | `id.indonesian` (Kemenag RI) |
| 📚 Tafsir | Tafsir Jalalayn, Bahasa Indonesia | `id.jalalayn` |
| 🔤 Transliterasi | Bacaan latin per ayat | `en.transliteration` |

Data di `app/src/main/assets/verses.txt` — satu ayat per baris, dipisah tab —
dibaca *streaming* saat pertama dijalankan, lalu disimpan ke Room. Jumlah ayat
tiap surah diverifikasi terhadap `surahs.json` (114 surah, 6.236 ayat, tidak ada
teks Arab kosong) oleh test otomatis.

**Yang jalan 100% offline:** seluruh teks Qur'an, terjemahan, tafsir, transliterasi,
pencarian ayat & surah, kalender hijriah, jadwal sholat, kompas kiblat, tasbih,
doa, kuis, dan khatam tracker.

> **Batas yang jujur:** *audio murottal* masih di-*stream* dari CDN EveryAyah saat
> diputar, jadi butuh koneksi. Satu-satunya fitur yang menyentuh jaringan adalah
> pemutar dan pengunduh audio — tidak ada analitik, pelacak, atau Crashlytics.

---

## 📱 Spesifikasi

| | |
| :--- | :--- |
| **Versi** | `1.0` (`versionCode 1`) |
| **Android** | 7.0 Nougat (API 24) → target API 36 |
| **Bahasa** | Kotlin 2.2.10 (K2), compiler Compose |
| **UI** | Jetpack Compose Material 3 — tiga tema: OLED gelap, Terang, Sepia |
| **Penyimpanan** | Room 2.7 + indeks **FTS4** (ayat & surah) |
| **Audio** | `android.media.MediaPlayer` (bawaan platform, tanpa dependensi) |
| **Izin** | Sholat & kiblat: kamera, sensor kompas, getaran · Pengingat: notifikasi |
| **Iklan / akun / telemetri** | Tidak ada |
| **Lisensi** | GNU AGPL v3.0 |

---

## ✨ Fitur

<details>
<summary><b>🌙 Beranda</b></summary>

- Ayat hari ini yang berganti otomatis tiap hari (rotasi deterministik per tanggal, bukan acak)
- Terakhir dibaca — langsung melompat ke posisi terakhir
- Waktu sholat berikutnya, dihitung astronomis di perangkat dan disegarkan tiap menit
- Kalender hijriah berbasis algoritma tabular, diuji terhadap tanggal sidang isbat Indonesia
- Hitung mundur hari besar Islam — tanggal Masehi diturunkan dari tanggal hijriah, bukan diketik manual
- Strip doa harian dan pintasan cepat ke Dzikir, doa, kuis, serta khatam
</details>

<details>
<summary><b>📖 Al-Qur'an & Pembaca</b></summary>

- **114 surah** + **30 juz**, bookmark surah & ayat, penanda hafalan
- Dua mode baca:
  - **Per ayat** — kartu dengan terjemahan, transliterasi, tafsir ringkas, dan nomor ayat hiasan
  - **Mushaf utuh** — teks mengalir, dipaged 25 ayat per blok agar tetap ringan
- **Mode sembunyikan** (hafalan): teks Arab disembunyikan sampai ayat ditekan
- **Penyorot tajwid** 6 warna: Ghunnah, Ikhfa, Idgham, Iqlab, Qalqalah, Mad — lengkap dengan legenda
- Ukuran font Arab, transliterasi, dan terjemahan bisa diatur
- **Pemain murottal** — 4 qari: Al-Afasy, As-Sudais, Al-Husary, Al-Ghamidi
  - kecepatan putar 0,75× – 1,5×
  - pengulang ayat 1× / 3× / 5× / 10×
  - *sleep timer*, dan unduh surah untuk didengarkan luring
- Tafsir lengkap per ayat dalam bottom sheet, bisa disalin
- Pencarian global untuk ayat maupun surah
</details>

<details>
<summary><b>🕌 Ibadah harian</b></summary>

- Jadwal sholat **6 waktu** (Subuh, Terbit, Dzuhur, Ashar, Maghrib, Isya) dihitung astronomis
- Kota sholat dapat dipilih, tersimpan di perangkat
- Kompas kiblat dengan umpan balik getar saat mengarah ke Ka'bah
- Tasbih digital dengan getar dan target hitungan
- **12 doa** Al-Qur'an terkategori (Rabbana, Para Nabi, Perlindungan, Rezeki, Taubat) + pencarian
- **12 Asmaul Husna** dengan makna dan penjelasannya
- Kuis pilihan ganda (9 soal) untuk latihan hafalan & mufradat
</details>

<details>
<summary><b>📊 Progres & Pengaturan</b></summary>

- **Khatam Tracker** — ceklis 30 juz, cincin progres beranimasi, target hari (30/60/90/120)
- **Pengingat harian** tilawah dengan jam yang bisa diatur
- **Kelola unduhan** — lihat, unduh, dan hapus audio per surah
- **Tema** — Gelap OLED / Terang / Sepia
- Qari pilihan, ukuran font, dan toggle penyorot tajwid
</details>

---

## 📥 Unduh

Ambil **`QuranKita.apk`** (sekitar 4,3 MB) dari
**[GitHub Releases](https://github.com/rynaqrtz/QuranKita/releases/latest)**.
Pasang langsung di perangkat —Izinkan "install dari sumber tidak dikenal" saat
dibuka pertama kali.

Tiap *push* ke `main` dan tiap tag `v*` memicu GitHub Actions yang:

1. menjalankan **17 unit test** (JVM + Robolectric) di lingkungan headless,
2. membangun APK rilis lewat `assembleCiRelease` (R8 + resource shrink + baseline profile),
3. menamainya menjadi `QuranKita.apk` dan mengunggah artefak build, dan
4. — **hanya untuk tag `v*`** — menerbitkan GitHub Release berisi berkas tersebut.

```bash
git clone https://github.com/rynaqrtz/QuranKita.git
cd QuranKita
./gradlew testDebugUnitTest     # jalankan test
./gradlew assembleDebug         # bangun APK debug (untuk pengembangan)
./gradlew assembleCiRelease     # bangun APK rilis (R8 + shrink + baseline profile)
git tag v1.0.0 && git push origin v1.0.0   # terbitkan rilis
```

---

## 🚀 Performa

Salah satu fokus utama proyek ini: **menjalankan di perangkat entry-level** (RAM
2 GB, Snapdragon 400-series) tanpa*nge-lag. Yang dilakukan:

| Teknik | Dampak |
| :--- | :--- |
| **R8 + resource shrink** | APK **20,8 MB → 4,3 MB**; file `.dex` **15 → 1**; `debuggable` dimatikan |
| **Baseline profile** | AGP menyematkan `baseline.prof` → ART AOT-compile jalur startup, tidak menunggu kompilasi JIT |
| **Seed streaming** | Data ayat dibaca per baris, bukan sebagai pohon JSON penuh (±30 MB heap) |
| **Tanpa animasi `infiniteRepeatable`** | Layar loading & dialogEMI tidak lagi menggambar ulang tiap frame |
| **Bayangan kartu dimatikan** | 27 `cardElevation` → 0; tiap kartu baru saat scroll tidak perlu rasterisasi shadow |
| **Mushaf dipaged** | 25 ayat per item lazy, bukan 286 ayat sekaligus |
| **Animasi lazy item** | `Modifier.animateItem()` — halus saat item berpindah, biaya nyaris nol |

Semua ini diverifikasi otomatis oleh CI: setiap *push* menjalankan 17 test,
membangun APK, dan hanya émisikan rilis bila semuanya hijau.

---

## 🛠️ Tech stack

| Teknologi | Peran |
| :--- | :--- |
| **Kotlin 2.2.10 (K2)** | Bahasa; compiler Compose |
| **Jetpack Compose Material 3** | UI; ornamen latar digambar prosedural dengan `Canvas` (bukan bitmap) |
| **Room 2.7 + FTS4** | Penyimpanan & pencarian lokal |
| **Coroutines + Flow** | State; `flatMapLatest` menjaga ayat selalu mengikuti surah aktif |
| **`android.media.MediaPlayer`** | Audio — tanpa dependensi pemutar pihak ketiga |
| **JUnit 4 + Robolectric 4.16** | Test — jalan di JVM CI, tanpa emulator |
| **GitHub Actions** | CI/CD: test → build → artefak → rilis otomatis |

**Tidak ada** Firebase, Retrofit, OkHttp, atau Moshi — tidak satu pun direferensikan
dari `build.gradle.kts` maupun kode sumber, jadi tidak ikut terpasang.

---

## 🧪 Test

```bash
./gradlew testDebugUnitTest
```

Laporan HTML: `app/build/reports/tests/testDebugUnitTest/index.html`

17 test yang mencakup:
- **kelengkapan data** — 114 surah / 6.236 ayat, teks Arab tidak boleh kosong
- **alur pembaca** — ayat mengikuti surah aktif; buka surah tidak menimpa posisi terakhir
- **kalender hijriah** — roundtrip Gregorian utuh + kecocokan dengan tanggal sidang isbat
- **pemetaan entitas** FTS, tajwid, juz, qari, doa, Asmaul Husna, hari besar Islam
- **tata letak berkas audio** luring

---

## 📁 Struktur proyek

```
app/src/main/java/com/example/
├── MainActivity.kt
├── data/
│   ├── local/            # AppDatabase (Room), DAO + entitas FTS4
│   ├── model/            # Surah, Verse, Qari, JuzInfo, QuranDua, AsmaulHusnaItem, …
│   └── repository/       # QuranRepository — satu-satunya sumber kebenaran
├── ui/
│   ├── components/       # AudioPlayerBar, SurahCard, IslamicEventsDialog, …
│   ├── home/  quran/  reader/  search/  settings/
│   ├── tahfidz/  khatam/  prayer/  dzikir/  dua/  asmaulhusna/  quiz/
│   ├── navigation/       # NavGraph, rute, shared-element transitions
│   └── theme/            # 3 palet (dark/light/sepia), tipografi Amiri & Inter
└── util/                 # AudioPlayerManager, HijriCalendar, PrayerTimeCalculator,
                          # TajwidFormatter, PrayerSettings, ThemeSettings

app/src/main/assets/
├── surahs.json           # metadata 114 surah
└── verses.txt            # 6.236 ayat (teks+terjemah+tafsir+latin), satu ayat per baris
```

---

## 🗺️ Roadmap

**Selesai**
- ✅ Al-Qur'an lengkap (6.236 ayat) terpasang offline di dalam APK
- ✅ CI/CD: test → build → artefak → GitHub Release otomatis
- ✅ Kalender hijriah, kota sholat bersama, pencarian FTS untuk ayat & surah
- ✅ Unduh audio per surah untuk didengarkan luring
- ✅ Animasi transisi antar layar (shared element + spring slide/fade)
- ✅ Tiga tema: OLED gelap, Terang, Sepia
- ✅ APK minified + resource shrink + baseline profile (4,3 MB, satu `.dex`)

**Ide berikutnya**
- ⬜ Baseline profile lanjutan dari macrobenchmark (butuh perangkat/emulator)
- ⬜ Audio luring: jeda/lanjutkan unduhan, unduh beberapa surah sekaligus
- ⬜ Mode baca hafalan dengan *checkpoint* per ayat

---

## 📄 Lisensi

**GNU AGPL v3.0** — lihat [`LICENSE`](LICENSE) di repositori ini.
