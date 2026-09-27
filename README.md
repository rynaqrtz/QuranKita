# 📖 QuranKita (Al-Qur'anul Karim & Islamic Daily Companion)

<div align="center">

![QuranKita Banner](https://img.shields.io/badge/QuranKita-v1.2.0%20Release-10B981?style=for-the-badge&logo=android&logoColor=white)
![Build Status](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions%20Passing-22C55E?style=for-the-badge&logo=githubactions&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Room Database](https://img.shields.io/badge/Room%20DB-FTS4%20Offline-F59E0B?style=for-the-badge&logo=sqlite&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-06B6D4?style=for-the-badge)

**Aplikasi Al-Qur'anul Karim & Pendamping Ibadah Harian Modern, Bersih, 100% Bebas Iklan, dan Offline-First.**

[📥 Unduh APK Rilis Terbaru](#-unduh-aplikasi-apk-build--github-releases) • [✨ Fitur Unggulan](#-fitur-unggulan) • [🛠️ Analisis & Alasan Tech Stack](#-arsitektur--alasan-pemilihan-tech-stack) • [🚀 Panduan Kompilasi](#-panduan-kompilasi-lokal)

</div>

---

## 📌 Informasi Versi Rilis

* **Versi Saat Ini:** `v1.2.0` (Produksi Stabil)
* **Target OS:** Android 8.0 (API Level 26) hingga Android 15 (API Level 35)
* **Karakteristik:** 100% Offline-First, Zero Data Tracking, Pure OLED Dark Mode.

---

## 📥 Unduh Aplikasi (APK Build & GitHub Releases)

Aplikasi ini telah dikonfigurasi dengan **GitHub Actions Automated CI/CD Pipeline**. Setiap kali ada pembaruan kode atau pembuatan tag rilis (*misal `v1.2.0`*), GitHub Actions akan secara otomatis:
1. Menjalankan seluruh pengujian unit (*Robolectric & JVM unit tests*).
2. Melakukan kompilasi berkas `app-debug.apk` dan `app-release.apk`.
3. Mengunggah berkas APK ke menu **GitHub Releases** dan **Actions Artifacts** sehingga dapat diunduh langsung ke perangkat Android Anda.

> 🔗 **Cara Mengunduh:** Kunjungi tab **Releases** pada repositori GitHub ini, lalu unduh berkas `app-debug.apk` atau `QuranKita-v1.2.0.apk` pada rilis terbaru.

---

## ✨ Fitur Unggulan

### 1. 🌙 Hitung Mundur Ramadhan & Kalender Hari Besar Islam
* **Countdown Real-time:** Menampilkan hitung mundur hari menuju **Awal Bulan Suci Ramadhan**, Hari Raya Idul Fitri, Hari Raya Idul Adha, dan Hari Arafah.
* **Kalender Hari Besar:** Daftar lengkap peringatan Islam (Tahun Baru Hijriah 1 Muharram, Nuzulul Qur'an, Isra Mi'raj, Hari Asyura & Tasu'a, Maulid Nabi).
* **Panduan Amalan Sunnah:** Penjelasan fadhilah dan rekomendasi amalan yang dianjurkan pada setiap momen mulia.

### 2. 🎨 Pewarnaan Kaidah Tajwid Visual (*Visual Tajweed Rules Highlighter*)
* **Sakelar Dinamis:** Dapat diaktifkan atau dinonaktifkan langsung melalui menu Pengaturan.
* **Panduan Warna Lengkap (*Tajweed Legend*):**
  * 🟢 **Ghunnah** (*Nun & Mim bertasydid `نّ` `مّ`*)
  * 🔵 **Ikhfa Haqiqi** (*Nun Mati & Tanwin `ً` `ٌ` `ٍ`*)
  * 🟡 **Idgham Bighunnah / Bilaghunnah** (`وّ` `يّ` `لّ` `رّ`)
  * 🔴 **Qalqalah** (*Huruf Qaf, Tha, Ba, Jim, Dal `ق ط ب ج د`*)
  * 🟣 **Mad Wajib & Jaiz** (*Tanda Alif & Bendera Mad `آ` `ى` `ٓ` `ٰ`*)

### 3. 📊 Dasbor Khatam Tracker (*Circular Progress Ring & 30 Juz Checklist*)
* **Lingkaran Progres Interaktif (*Circular Progress Ring*):** Visualisasi persentase capaian khatam Al-Qur'an dan total Juz yang telah selesai dibaca.
* **Daftar Centang 30 Juz:** Checklist 30 Juz dengan navigasi instan 1-ketuk ke awal surah tiap Juz.
* **Pelacak Streak Tilawah & Perencana Target:** Pengaturan target khatam (30, 60, 90, 120 hari) beserta kalkulasi beban bacaan per sholat fardhu.
* **Kartu Lanjutkan Membaca (*Resume Reading*):** Melanjutkan bacaan tepat pada surah dan ayat terakhir yang ditinggalkan.

### 4. 🤲 Kumpulan Doa Al-Qur'an Terkategori (*Categorized Quranic Duas*)
* **Kategori Lengkap:** Doa *Rabbana*, Doa *Para Nabi*, Doa *Perlindungan & Keselamatan*, Doa *Rezeki & Keberkahan*, serta Doa *Istighfar & Taubat*.
* **Fitur Pencarian:** Cari doa secara instan berdasarkan judul, ayat, teks Arab, Latin, maupun terjemahan.
* **Salin & Bagikan:** Membagikan teks doa lengkap ke media sosial atau menyalinnya ke papan klip.

### 5. 🎵 Audio Murottal, Sleep Timer & Pengatur Kecepatan
* **4 Qari Internasional:** Misyari Rasyid Al-Afasy, Abdurrahman As-Sudais, Mahmud Khalil Al-Husary, dan Saad Al-Ghamidi.
* **Sleep Timer:** Pengatur waktu otomatis mati (*15m, 30m, 45m, 60m*) untuk mendengarkan sebelum tidur.
* **Pengatur Kecepatan Putar (*Playback Speed*):** Pilihan tempo audio (*0.75x, 1.0x, 1.25x, 1.5x*) untuk mempermudah belajar makhraj huruf.
* **Tahfidz Loop:** Pengulangan ayat (*1x, 3x, 5x, 10x*) untuk muraja'ah hafalan.

### 6. 🔍 Pencarian Teks Penuh Cepat (Full-Text Search FTS4)
* Pencarian cepat surah, nomor ayat, transliterasi Latin, dan arti terjemahan bahasa Indonesia langsung dari bilah pencarian Beranda.

### 7. 🕌 Jadwal Sholat Presisi & Kompas Kiblat
* Perhitungan hisab astronomis offline untuk 5 waktu sholat dan imsakiyah dengan kompas sensor magnetometer.

---

## 🛠️ Arsitektur & Alasan Pemilihan Tech Stack

Berikut adalah analisis teknis komprehensif mengenai pustaka dan teknologi yang dipilih dalam pengembangan aplikasi **QuranKita**:

| Teknologi / Pustaka | Peran dalam Proyek | Mengapa Dipilih & Alasannya (*Why & Justification*) |
| :--- | :--- | :--- |
| **Kotlin 2.0** | Bahasa Pemrograman Utama | Bahasa resmi Android modern dengan fitur *null-safety*, ekstensi fungsional, sintaks ringkas, serta performa kompilasi K2 compiler yang sangat cepat dan stabil. |
| **Jetpack Compose (M3)** | Antarmuka Pengguna (*UI Toolkit*) | Menggantikan sistem XML lama dengan paradigma *declarative UI*. Memungkinkan antarmuka *Pure Black OLED*, animasi halus, dan *recomposition* yang efisien tanpa beban *view hierarchy* yang berat. |
| **Room Database + SQLite FTS4** | Penyimpanan Lokal & Mesin Pencarian | Tabel virtual FTS4 (*Full-Text Search*) mengindeks 6.236 ayat dan 114 surah sehingga pencarian teks selesai dalam waktu <15ms tanpa membutuhkan server backend (*100% offline-first*). |
| **Kotlin Coroutines & Flow** | Concurrency & Reactive State | Menghindari *callback hell* dan kebocoran memori. `StateFlow` dan `SharedFlow` memastikan UI State sinkron secara reaktif dengan siklus hidup komponen Android (*Lifecycle-aware*). |
| **Android AlarmManager** | Penjadwal Pengingat Tilawah Harian | Memastikan notifikasi pengingat tilawah berbunyi secara presisi (*exact timing*) pada waktu yang diinginkan pengguna meskipun aplikasi sedang ditutup atau perangkat dalam kondisi *Doze mode*. |
| **Android MediaPlayer API** | Engine Pemutar Murottal Audio | API bawaan Android yang ringan, mendukung pemutaran lokal/streaming, integrasi *playback speed* (`PlaybackParams`), dan kontrol *looping* tanpa dependensi pustaka pihak ketiga yang membengkakkan ukuran APK. |
| **Clean Architecture (MVVM Pattern)** | Pola Desain Perangkat Lunak | Memisahkan kode menjadi lapisan independen (*UI, ViewModel, Repository, Local Data Source*). Hal ini membuat kode mudah diuji (*testable*), dirawat (*maintainable*), dan dikembangkan di masa mendatang. |
| **GitHub Actions** | Otomasi CI/CD & Rilis APK | Mengotomatiskan proses pengujian unit, kompilasi APK, dan publikasi rilis setiap kali terjadi perubahan kode di repositori utama. |

---

## 📁 Struktur Direktori Proyek

```
com.example/
├── data/
│   ├── local/
│   │   ├── dao/             # QuranDao (Room DAO dengan query FTS4 & Flow)
│   │   ├── entity/          # SurahEntity, VerseEntity, LastReadEntity, DownloadedAudioEntity
│   │   └── AppDatabase.kt   # Instance Room Database
│   ├── model/               # Model data domain (IslamicEvent, QuranDua, JuzInfo, Qari, AsmaulHusna)
│   └── repository/          # QuranRepository (Single Source of Truth)
├── ui/
│   ├── components/          # Komponen M3 (AudioPlayerBar, IslamicEventsDialog, TixarLogo, SurahCard)
│   ├── home/                # Beranda, Salam, Kartu Hitung Mundur Ramadhan, Last Read
│   ├── khatam/              # Dasbor Khatam Tracker (Progress Ring & 30 Juz Checklist)
│   ├── quran/               # Tab Daftar Surah (114), 30 Juz, Penanda Bookmark
│   ├── reader/              # SurahReaderScreen, Tafsir Modal Sheet, Mushaf View
│   ├── dua/                 # Kumpulan Doa Al-Qur'an Terkategori & Pencarian
│   ├── asmaulhusna/         # 99 Asmaul Husna & Penjelasan Makna
│   ├── quiz/                # Kuis Tajwid & Hafalan Ayat
│   ├── prayer/              # Jadwal Sholat 5 Waktu & Kompas Arah Kiblat
│   ├── settings/            # Visual Tajweed Highlighter, Pengaturan Font, Alarm Tilawah
│   └── theme/               # Pure OLED Theme, Tipografi Font Amiri & Inter
└── util/                    # AudioPlayerManager, PrayerCalculator, QuranReminderManager, TajwidFormatter
```

---

## 🚀 Panduan Kompilasi Lokal

1. **Prasyarat Lingkungan:**
   * Android Studio Ladybug / Koala atau versi terbaru.
   * JDK 17 (Java Development Kit).
   * Android SDK Platform API 35.

2. **Langkah Kompilasi:**
   ```bash
   # Clone repositori
   git clone https://github.com/username/qurankita.git
   cd qurankita

   # Menjalankan pengujian unit otomatis
   gradle testDebugUnitTest

   # Membangun file APK Debug
   gradle assembleDebug
   ```

3. Berkas APK hasil kompilasi akan berada di direktori:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah lisensi **MIT License**. Terbuka untuk pengembangan komunitas dan kemaslahatan umat.
