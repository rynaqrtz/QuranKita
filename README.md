# 📖 QuranKita

<div align="center">

<img src="https://cdn.aceimg.com/9ZQ31x8nD.png" alt="QuranKita" width="100%">

![Version](https://img.shields.io/badge/version-1.0-10B981?style=for-the-badge&logo=android&logoColor=white)
![CI](https://img.shields.io/github/actions/workflow/status/rynaqrtz/QuranKita/android-build.yml?branch=main&label=build&style=for-the-badge&logo=githubactions&logoColor=white)
![Min SDK](https://img.shields.io/badge/Android-7.0%20%2B%20(API%2024)-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Tests](https://img.shields.io/badge/tests-18%20passed-22C55E?style=for-the-badge&logoColor=white)
![License](https://img.shields.io/badge/license-AGPL--3.0-blue?style=for-the-badge)

**Aplikasi Al-Qur'an & pendamping ibadah harian — seluruh 6.236 ayat terpasang di dalam APK, tanpa iklan, tanpa akun, tanpa telemetri.**

[Baca cepat](#-yang-membuatnya-berbeda) • [Fitur](#-fitur) • [Unduh](#-unduh) • [Bangun sendiri](#-bangun-sendiri) • [Struktur proyek](#-struktur-proyek)

</div>

---

## 🌟 Yang membuatnya berbeda

Kebanyakan aplikasi Al-Qur'an mengunduh teks, terjemahan, dan tafsir dari server saat
pertama dibuka. QuranKita **membundel semuanya ke dalam APK**:

| | Isi | Sumber |
| :--- | :--- | :--- |
| 📜 Teks Arab | 6.236 ayat, rasm Utsmani | `quran-uthmani` |
| 🇮🇩 Terjemahan | Bahasa Indonesia | `id.indonesian` (Kemenag RI) |
| 📚 Tafsir | Tafsir Jalalayn, Bahasa Indonesia | `id.jalalayn` |
| 🔤 Transliterasi | Bacaan latin per ayat | `en.transliteration` |

Berkas `app/src/main/assets/initial_verses.json` (6,3 MB) ditulis ulang langsung dari
keempat edisi di atas, dengan jumlah ayat tiap surah diverifikasi terhadap
`surahs.json`. **Hasilnya: membaca, mencari, menyalin tafsir, dan melihat jadwal
sholat semuanya jalan tanpa satu paket data pun yang diunduh.**

> Pengecualian jujur: **murottal audio** tetap di-*stream* dari CDN EveryAyah, dan bisa
> diunduh per surah lewat tombol unduh pada bilah pemutar.

---

## 📱 Spesifikasi

* **Versi** — `1.0` (`versionCode 1`)
* **Android** — 7.0 Nougat (API 24) s/d target API 36
* **Basis data** — Room 2.7 + indeks **FTS4** untuk pencarian ayat *dan* surah
* **UI** — Jetpack Compose Material 3, tema OLED gelap tunggal
* **Iklan / tracking / akun** — tidak ada

---

## ✨ Fitur

<details>
<summary><b>🌙 Beranda</b></summary>

- Ayat hari ini yang berganti otomatis tiap hari (rotasi deterministik, bukan acak)
- Terakhir dibaca, langsung melompat ke posisi terakhir
- Waktu sholat berikutnya yang dihitung ulang tiap menit
- Kalender hijriah berbasis algoritma tabular, dikalibrasi terhadap tanggal
  sidang isbat Indonesia (selisih ≤ 1 hari pada semua titik uji)
- Hitung mundur hari besar Islam — tanggal Masehi-nya diturunkan dari tanggal
  hijriahnya, bukan diketik manual
</details>

<details>
<summary><b>📖 Al-Qur'an & Pembaca</b></summary>

- 114 surah, 30 juz, bookmark surah & ayat
- Dua mode: **per-ayat** (terjemahan, transliterasi, tafsir, nomor ayat hiasan)
  dan **mushaf utuh**
- Penanda hafalan (tandai ayat sudah dihafal) + mode sembunyikan Arab sampai ditekan
- Penyorot tajwid: Ghunnah, Ikhfa, Idgham, Qalqalah, Mad — dengan legenda warna
- Ukuran font Arab/translulerasi/terjemahan bisa diatur
- Pemain murottal: kecepatan 0,75×–1,5×, pengulang 1×–10×, *sleep timer*,
  4 qari (Al-Afasy, As-Sudais, Al-Husary, Al-Ghamidi)
- **Unduh surah untuk dibaca luring** — tombol unduh di bilah pemutar
</details>

<details>
<summary><b>🕌 Ibadah harian</b></summary>

- Waktu sholat 5 waktu + imsak + dhuha, dihitung astronomis di perangkat
- Kompas kiblat dengan getaran haptik saat sejajar Ka'bah
- Tasbih digital dengan umpan balik getar
- Kumpulan doa Al-Qur'an terkategori + pencarian
- 99 Asmaul Husna lengkap dengan maknanya
- Kuis tajwid & hafalan
</details>

<details>
<summary><b>📊 Progres & Pengaturan</b></summary>

- Khatam Tracker: ceklis 30 juz, cincin progres, target hari tilawah
- Pencarian global (FTS4) untuk **ayat maupun surah** — cari "Yasin", langsung
  muncul surahnya
- Kelola unduhan audio (unduh / hapus per surah)
- Pengingat harian tilawah yang bisa dijadwalkan
</details>

---

## 📥 Unduh

Ambil APK dari **[GitHub Releases](../../releases)**.

Tiap *push* ke `main` dan tiap tag `v*` memicu GitHub Actions yang:

1. menjalankan seluruh **18 unit test** (JVM + Robolectric) di lingkungan headless,
2. membangun `app-debug.apk` dan menamainya ulang menjadi `QuranKita.apk`,
3. mengunggahnya sebagai artefak build (30 hari), dan
4. — **hanya untuk tag `v*`** — menerbitkan **GitHub Release** berisi `QuranKita.apk`.

```bash
# menerbitkan rilis
git tag v1.0.0 && git push origin v1.0.0
```

---

## 🛠️ Tech stack

| Teknologi | Peran | Kenapa |
| :--- | :--- | :--- |
| **Kotlin 2.2.10 (K2)** | Bahasa | Tipe aman, kompilasi jauh lebih cepat |
| **Jetpack Compose M3** | UI | Tanpa hierarki XML; ornamen latar (motif Rub el Hizb, halo) digambar prosedural dengan `Canvas`, bukan bitmap |
| **Room 2.7 + FTS4** | Penyimpanan | Sinkron awal sekali dari aset, lalu pencarian sepenuhnya lokal |
| **Coroutines + Flow** | State | `flatMapLatest` menjaga pembaca ayat selalu mengikuti surah yang aktif |
| **`android.media.MediaPlayer`** | Audio | Pemutar bawaan platform — tidak menambah dependensi apa pun |
| **JUnit 4 + Robolectric 4.16** | Test | Test berjalan di JVM CI dalam hitungan menit, tanpa emulator |
| **GitHub Actions** | CI/CD | Test, build, artefak, rilis otomatis |

Tidak ada Firebase, Retrofit, OkHttp, atau Moshi — tidak satu pun direferensikan
dari kode sumber, jadi tidak ikut terpasang.

---

## 🧪 Test

```bash
./gradlew testDebugUnitTest
```

Laporan: `app/build/reports/tests/testDebugUnitTest/index.html`

Cakupan saat ini:

* alur pembaca — ayat mengikuti surah aktif, buka surah tidak menimpa posisi terakhir
* kelengkapan data — 114 surah / 6.236 ayat, teks Arab tidak boleh kosong
* kalender hijriah — cocok dengan pengumuman sidang isbat, roundtrip Gregorian utuh
* pemetaan entitas FTS, tajwid, juz, qari, doa, Asmaul Husna, hari besar Islam
* tata letak berkas audio luring

---

## 🚀 Bangun sendiri

Prasyarat: **JDK 17**, Android SDK (API 24–36).

```bash
git clone https://github.com/rynaqrtz/QuranKita.git
cd QuranKita

./gradlew testDebugUnitTest     # jalankan test
./gradlew assembleDebug         # bangun APK
```

APK (lokal): `app/build/outputs/apk/debug/app-debug.apk`

> GitHub Actions me-*rename* berkas itu menjadi **`QuranKita.apk`** sebelum
> diunggah dan dirilis.

> Repo ini sudah menyertakan **Gradle wrapper** dan **`debug.keystore`**, sehingga
> build berjalan di mesin mana pun tanpa konfigurasi tambahan.

---

## 📁 Struktur proyek

```
app/src/main/java/com/example/
├── data/
│   ├── local/            # AppDatabase (Room v3), DAO FTS4, entity
│   ├── model/            # Surah, Verse, Qari, IslamicEvent, JuzInfo, …
│   └── repository/       # QuranRepository — satu-satunya sumber kebenaran
├── ui/
│   ├── components/       # AudioPlayerBar, SurahCard, IslamicEventsDialog, …
│   ├── home/             # Beranda
│   ├── quran/            # Daftar surah / juz / bookmark
│   ├── reader/           # Pembaca surah, modal tafsir, mode mushaf
│   ├── tahfidz/  khatam/ # Penanda hafalan & khatam 30 juz
│   ├── prayer/           # Jadwal sholat & kompas kiblat
│   ├── dzikir/ dua/ asmaulhusna/ quiz/
│   ├── search/  settings/
│   ├── navigation/       # NavGraph + rute
│   └── theme/            # Palet OLED, tipografi Amiri & Inter
├── util/                 # AudioPlayerManager, HijriCalendar,
│                         # PrayerTimeCalculator, TajwidFormatter, …
└── MainActivity.kt

app/src/main/assets/
├── surahs.json           # metadata 114 surah
└── initial_verses.json   # 6.236 ayat lengkap (teks+terjemah+tafsir+latin)
```

---

## 🗺️ Roadmap

- ✅ Al-Qur'an lengkap (6.236 ayat) terpasang di dalam APK
- ✅ CI/CD: test → build → artefak → rilis otomatis
- ✅ Kalender hijriah, kota sholat bersama, pencarian FTS untuk ayat & surah
- ✅ Unduh surah untuk dibaca luring
- ✅ Pembersihan dependensi & aset tak terpakai
- 🚧 Animasi transisi antar layar
- ⬜ Mode terang dan sepia
- ⬜ Baseline profile untuk mempercepat startup

---

## 📄 Lisensi

**GNU AGPL v3.0** — lihat [`LICENSE`](LICENSE) di repositori ini.
