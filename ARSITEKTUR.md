# 🏛️ Arsitektur & Spesifikasi Teknis QuranKita

Dokumen ini menjelaskan rancang bangun perangkat lunak, pola desain, basis data, pipeline reaktif, dan sistem subsistem aplikasi **QuranKita**.

---

## 1. Pola Desain & Clean Architecture (MVVM + MVI-Like State)

Aplikasi dibangun dengan prinsip **Clean Architecture** berlapis untuk memastikan *separation of concerns*, *testability*, dan skalabilitas kode:

```
┌─────────────────────────────────────────────────────────┐
│                    UI LAYER (Compose)                   │
│   Composables ──► StateFlow (UI State) ──► User Actions │
└────────────────────────────┬────────────────────────────┘
                             │ Events & Subscriptions
┌────────────────────────────▼────────────────────────────┐
│                    VIEWMODEL LAYER                      │
│   HomeViewModel, QuranListViewModel, ReaderViewModel    │
│   SettingsViewModel, DzikirViewModel, PrayerViewModel   │
└────────────────────────────┬────────────────────────────┘
                             │ Coroutine Scope / Flow
┌────────────────────────────▼────────────────────────────┐
│                   REPOSITORY LAYER                      │
│   QuranRepository (Single Source of Truth)              │
└────────────────────────────┬────────────────────────────┘
                             │
            ┌────────────────┴────────────────┐
            ▼                                 ▼
┌───────────────────────┐         ┌───────────────────────┐
│     LOCAL STORAGE     │         │   SYSTEM & AUDIO      │
│  Room Database (FTS4) │         │  MediaPlayer / Alarm  │
│  SharedPreferences    │         │  Sensors (Qibla/GPS)  │
└───────────────────────┘         └───────────────────────┘
```

---

## 2. Struktur Basis Data (Room Database & FTS4)

Room Database (`AppDatabase.kt`) bertindak sebagai penyimpanan lokal tunggal (*offline-first*):

### Tabel & Relasi:
1. **`surahs` (`SurahEntity`)**:
   * Menyimpan data metadata 114 surah (nomor, nama latin, kaligrafi Arab, arti, jumlah ayat, tempat turun, status bookmark, dan status khatam).
2. **`surahs_fts` (`SurahFtsEntity`)**:
   * Tabel virtual **FTS4** untuk pencarian cepat *full-text search* nama surah, ejaan latin, dan terjemahan.
3. **`verses` (`VerseEntity`)**:
   * Menyimpan 6.236 ayat lengkap (nomor surah, nomor ayat, kaligrafi Arab bertashkeel, transliterasi Latin, terjemahan resmi Kemenag RI, tafsir ringkas, status bookmark, dan status hafalan).
4. **`verses_fts` (`VerseFtsEntity`)**:
   * Tabel virtual **FTS4** untuk pencarian instan kata kunci dalam ayat dan terjemahan.
5. **`last_read` (`LastReadEntity`)**:
   * Menyimpan posisi terakhir baca pengguna untuk pemulihan bacaan instan (*Resume Reading*).
6. **`downloaded_audios` (`DownloadedAudioEntity`)**:
   * Melacak berkas audio murottal yang telah tersimpan secara luring.

---

## 3. Subsistem Pemutar Audio & Sleep Timer (*Audio Playback Engine*)

* **Manajer Audio (`AudioPlayerManager.kt`)**:
  * Menggunakan `android.media.MediaPlayer` dengan *state machine* reaktif (`AudioPlaybackState`).
  * Mendukung pemutaran per ayat secara berurutan, siklus pengulangan hafalan (*Repeat Target: 1x, 3x, 5x, 10x*), dan *fallback streaming* CDN / *local cache*.
  * **Sleep Timer Engine:** Timer berbasis coroutine `kotlinx.coroutines` untuk menghentikan audio otomatis dalam 15, 30, 45, atau 60 menit.
  * **Playback Speed Engine:** Mengatur tempo audio (*0.75x s/d 1.5x*) via `android.media.PlaybackParams` (Android 6.0+).

---

## 4. Sistem Pengingat Tilawah Lokal (*Alarm & Notification System*)

* **`QuranReminderManager.kt` & `QuranReminderReceiver.kt`**:
  * Memanfaatkan `AlarmManager.setExactAndAllowWhileIdle()` dengan jadwal berulang setiap 24 jam.
  * Menangani pembuatan `NotificationChannel` dengan kompatibilitas penuh Android 8.0+ hingga Android 15.
  * Integrasi *PendingIntent* langsung ke rute Al-Qur'an dan posisi baca terakhir.

---

## 5. Mesin Pewarnaan Kaidah Tajwid Dinamis (*Visual Tajweed Rules Highlighter*)

* **`TajwidFormatter.kt`**:
  * Memproses teks Arab bertashkeel menggunakan *regex patterns* dan tokenisasi fonetik:
    * **Ghunnah** (Nun/Mim bertasydid `نّ` `مّ`) ➔ Hijau Emerald (*#10B981*)
    * **Ikhfa Haqiqi** (Nun mati/Tanwin `ً` `ٌ` `ٍ`) ➔ Biru Cyan (*#38BDF8*)
    * **Idgham Bighunnah / Bilaghunnah** (`وّ` `يّ` `لّ` `رّ`) ➔ Oranye Gold (*#F59E0B*)
    * **Qalqalah** (Huruf *Qaf, Tha, Ba, Jim, Dal* `ق ط ب ج د`) ➔ Merah Aksen (*#EF4444*)
    * **Mad Wajib / Jaiz** (`آ` `ى` `ٓ` `ٰ`) ➔ Ungu Violet (*#A855F7*)
  * Menghasilkan `AnnotatedString` yang dapat dihidupkan/dimatikan secara dinamis di pengaturan.

---

## 6. Dasbor Pelacak Khatam (*Khatam Tracker Engine*)

* **`KhatamTrackerScreen.kt`**:
  * Menghitung capaian tilawah 30 Juz secara komprehensif.
  * *Circular Progress Ring* kustom yang digambar dengan `androidx.compose.foundation.Canvas` dan *sweep gradient brush*.
  * Pelacak *Daily Tilawah Streak* dan estimasi waktu penyelesaian berdasarkan target rencana (*30, 60, 90, 120 hari*).
