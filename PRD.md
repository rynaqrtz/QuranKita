# PRD — QuranKita v2.0 (Full Repair + 3D UI/UX Upgrade)

**Status:** Audit selesai · Eksekusi berjalan
**Repo:** https://github.com/rynaqrtz/quran
**Metode:** audit → PRD → ticket → kerja berurutan → CI/CD (GitHub Actions jadi satu-satunya build machine)

---

## 1. Ringkasan Eksekutif

Aplikasi ini secara struktur sudah rapi (MVVM + Room + Compose, 12 rute, 11.900 baris) tetapi
memiliki **beberapa bug yang membuat fitur utamanya tidak berfungsi**, **klaim dokumen yang
tidak sesuai kode**, serta **UI yang datar** (belum 3D/premium).

Temuan audit dipisah menjadi 3 kelas:

| Kelas | Jumlah | Dampak |
|---|---|---|
| **P0 — Fitur rusak total** | 5 | Membaca surah, resume baca, sleep timer, dan CI semuanya gagal |
| **P1 — Fungsional salah** | 11 | Data salah, state basi, fitur mati, FTS tidak terpakai |
| **P2 — Kualitas / UI-UX** | 14 | Desain datar, mati gaya, dokumen menyesatkan, jejak AI build |

---

## 2. Hasil Audit

### 2.1 P0 — Fitur Rusak Total

**BUG-01 · Reader selalu menampilkan Al-Fatihah**
`ui/reader/SurahReaderViewModel.kt:61,67` — `currentSurah` dan `verses` di-`stateIn`
dengan `repository.getSurah(_currentSurahNumber.value)`. Nilai itu dibaca **satu kali saat
`init`** (selalu `1`) dan tidak pernah di-`flatMapLatest`. Akibatnya membuka surah apa pun
di layar reader tetap menampilkan surah 1.

**BUG-02 · Panjang surah & nama surah dipalsukan**
`SurahReaderViewModel.kt:78-84` — `ensureVersesLoaded(verseCount = 7, surahName = "Surah $n")`.
Placeholder dibuat maksimal 7 ayat meski surah aslinya 286 ayat, dan nama surah jadi
"Surah 2" alih-alih "Al-Baqarah".

**BUG-03 · "Lanjutkan Baca" selalu reset ke ayat 1**
`SurahReaderViewModel.kt:84` — `setLastRead(..., 1)` dipanggil setiap kali surah dibuka,
menimpa posisi terakhir yang tersimpan. Kartu *Terakhir Dibaca* di Home jadi tidak pernah
menunjukkan posisi sebenarnya.

**BUG-04 · Sleep timer mati saat ganti ayat**
`util/AudioPlayerManager.kt:243` — `stop()` memanggil `sleepTimerJob?.cancel()`, dan
`playVerse()` selalu memanggil `stop()` terlebih dulu. Timer 15/30/45/60 menit batal begitu
ayat berikutnya mulai diputar.

**BUG-05 · GitHub Actions tidak mungkin lulus**
- `gradlew` / `gradle-wrapper.jar` tidak ada di repo (hanya `gradle-wrapper.properties`).
  Workflow memanggil `gradle ...` padahal `ubuntu-latest` tidak punya Gradle.
- `debug.keystore` masuk `.gitignore` tetapi wajib ada untuk `assembleDebug`.

### 2.2 P1 — Fungsional Salah

| ID | Temuan | Lokasi |
|---|---|---|
| BUG-06 | Path audio offline per-surah (`{qari}_001.mp3`) dipakai untuk semua ayat; dan **tidak ada kode yang pernah membuat file itu** — cabang mati yang salah | `AudioPlayerManager.kt:129` |
| BUG-07 | `SettingsScreen` menerima `onThemeChanged` tapi **tidak pernah memanggilnya** — sakelar tema percuma | `SettingsScreen.kt:87,652` |
| BUG-08 | `AppThemeMode` hanya berisi `DARK`; palet Light & Sepia (11 warna) terdefinisi tapi tidak pernah terpasang → 21 warna mati | `theme/Color.kt`, `theme/Theme.kt:264` |
| BUG-09 | Home selalu pakai `DEFAULT_CITIES.first()` (Jakarta) meski pengguna memilih kota lain di layar Kiblat → dua layar menampilkan waktu sholat berbeda | `HomeViewModel.kt:81` |
| BUG-10 | Waktu sholat dihitung **sekali saat init**; setelah waktu sholat berikutnya lewat, "Sholat berikutnya" tetap basi | `HomeViewModel.kt:80`, `PrayerQiblaViewModel.kt` |
| BUG-11 | `getTodayHijriDate()` bukan konversi hijriah — tahun dikunci `1448`, hari `(day+12)%29` | `PrayerTimeCalculator.kt:159` |
| BUG-12 | Kartu Ramadhan menampilkan tanggal Gregorian hardcode "18 Feb 2027" | `IslamicEventsDialog.kt` |
| BUG-13 | `VerseOfTheDay` selalu QS. Al-Baqarah 286, tidak pernah berganti | `HomeViewModel.kt` |
| BUG-14 | FTS4 dirender tetapi **tidak pernah dipanggil** — pencarian memakai `LIKE`; `searchVersesFts`/`searchSurahsFts` mati. README mengklaim pencarian FTS <15ms | `SearchViewModel.kt:34` |
| BUG-15 | `registerDownloadedAudio` tidak pernah dipanggil → "Kelola Unduhan Audio" selalu kosong | `SettingsViewModel.kt:52` |
| BUG-16 | LazyList tanpa `key` (rekomposisi tak efisien) | `SearchScreen.kt:138`, `PrayerQiblaScreen.kt:223` |

### 2.3 P2 — UI/UX, Desain, Kualitas

| ID | Temuan |
|---|---|
| UX-01 | **Tidak ada rasa 3D/premium.** Semua kartu datar (border 1dp, elevation 0). Tidak ada depth, tilt, parallax, atau shared-element transition antar layar |
| UX-02 | Animasi hanya "denyut opsin tak henti" (`rememberInfiniteTransition` di kartu Ramadhan & splash) — boros recomposisi, tidak ada makna transisi |
| UX-03 | Navigasi `Home → Reader` tanpa shared-element: loncat mendadak, tidak terasa premium |
| UX-04 | Progress bar & tombol utama tanpa *spring*/fisika — gerak linier datar |
| UX-05 | Kontras legend tajwid di `SettingsScreen` memakai warna tema; kode warna tajwid **berbeda** dari yang tertulis di `README.md` & `ARSITEKTUR.md` |
| UX-06 | `MainActivity` state tema tidak dipersist; reset ke DARK saat proses dibuat ulang |
| UX-07 | Splash menahan minimal 900 ms secara artifisial walau data sudah siap |
| UX-08 | Tanpa `contentDescription` pada banyak ikon dekoratif; beberapa target sentuh < 48dp |
| UX-09 | `BackHandler` ada, tapi tidak ada animasi transisi masuk/keluar yang konsisten antar rute |
| CLEAN-01 | Kode mati: `BottomNavBar.kt` (113 ln), `AyahQuoteCreatorDialog.kt` (170 ln), `AppLoadingScreen`, `QuranAppLogoBadge`, `TajwidIqlab`, 21 warna, warna `Tixar*` |
| CLEAN-02 | Dependensi terpasang tapi tak pernah dipakai: `retrofit`, `moshi`, `okhttp`, `logging-interceptor`, `firebase-ai`, `firebase-appcheck-*`, plugin `secrets`, plugin `google-services` |
| CLEAN-03 | Duplikasi: `setToggle` pada `revealedVerses`/`expandedTafsir`; alias warna (`EmeraldPrimary`==`EmeraldVibrant`==`EmeraldGreen`) |
| CLEAN-04 | README v1.2.0 / targetSdk 36 vs `versionName "1.0"`; klaim "6.236 ayat" padahal hanya 38 ayat nyata di aset |
| AI-01 | Jejak AI build: `metadata.json` → `MAJOR_CAPABILITY_SERVER_SIDE_GEMINI_API`; `.env.example` → `GEMINI_API_KEY`; dependensi Firebase AI/AppCheck |

### 2.4 Ponytail — Audit Over-engineering

```
delete: BottomNavBar.kt (duplikat, tidak direferensikan). Tidak ada gantinya. [ui/components/BottomNavBar.kt]
delete: AyahQuoteCreatorDialog.kt (tidak pernah dipanggil). Tidak ada gantinya. [ui/components/AyahQuoteCreatorDialog.kt]
delete: 21 warna tak terpakai + 3 alias identik Emerald*. Tidak ada gantinya. [ui/theme/Color.kt]
yagni: ReaderSettings.readerTheme, SettingsViewModel.setThemeMode, onThemeChanged plumbing — enum cuma 1 nilai DARK. Hapus seluruh rantai.
yagni: DownloadedAudioEntity + registerDownloadedAudio — tidak ada fitur unduh. Hapus tabel atau bangun fiturnya.
yagni: searchVersesFts / searchSurahsFts — FTS dibangun tapi tak pernah dipakai. Pakai, atau hapus tabel FTS.
native: retrofit + moshi + okhttp + logging-interceptor + firebase-ai + appcheck — platform tidak butuh, kode tidak panggil. Hapus.
shrink: toggleRevealVerse / toggleExpandTafsir — logika Set kembar jadi 1 helper.
shrink: 900ms splash delay artifisial. Hapus, pakai kondisi data saja.
net: -400 lines, -8 deps possible.
```

---

## 3. Ticket (Urutan Eksekusi)

Kerja berurutan. Setiap fase harus hijau di GitHub Actions sebelum lanjut.

### Fase 0 — Unblock Build & CI  `wajib pertama`
- **T-001** Commit `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`
- **T-002** Berhenti meng-ignore `debug.keystore`, commit keystore debug (standar Android)
- **T-003** Perbaiki workflow: pakai `./gradlew`, jangan buat Release otomatis tiap push ke main
- **T-004** Push baseline → pantau Actions sampai lulus

### Fase 1 — Bug P0  `fitur inti`
- **T-010** BUG-01: `flatMapLatest` supaya reader mengikuti surah aktif
- **T-011** BUG-02: `ensureVersesLoaded` pakai `nameLatin` + `verseCount` asli dari DB
- **T-012** BUG-03: jangan timpa last-read saat surah dibuka; hanya update saat scroll
- **T-013** BUG-04: sleep timer tidak boleh dibatalkan ganti ayat
- **T-014** Uji: 3 unit test untuk ketiga alur di atas

### Fase 2 — Bug P1  `data & state`
- **T-020** BUG-09 + BUG-10: satu sumber kota & jadwal, refresh saat melewati waktu
- **T-021** BUG-11 + BUG-12: konversi hijriah tabular yang benar, tanggal event dihitung dari tanggal
- **T-022** BUG-14: alihkan pencarian ke FTS dengan fallback `LIKE`
- **T-023** BUG-13: ayat hari ini berdasarkan tanggal
- **T-024** BUG-16: tambah `key` pada LazyList
- **T-025** BUG-07 + BUG-06 + BUG-15: putuskan (bangun atau hapus) — lihat T-050

### Fase 3 — UI/UX 3D & Animasi  `upgrade visual`
- **T-030** Design token 3D: elevation bertingkat, gradasi permukaan, bayangan diffus, sudut membulat konsisten
- **T-031** Lapisan `graphicsLayer` 3D — tilt/perspective halus pada kartu hero & quick-action
- **T-032** Shared-element transition Home ⇄ Reader + animasi rute masuk/keluar
- **T-033** Animasi ber-fisika: `spring` untuk tombol, `animateFloatAsState` untuk progress, stagger reveal daftar
- **T-034** Splash & loading: ganti delay 900ms artifisial dengan sinkronisasi data + animasi keluar bermakna
- **T-035** Audit aksesibilitas: kontras 4.5:1, `contentDescription`, target sentuh 48dp
- **T-036** Tulis `DESIGN_SYSTEM.md` (token warna, tipografi, elevasi, easing, aturan 3D)

### Fase 4 — Kebersihan & Hapus Jejak AI  `cleanup`
- **T-050** Hapus kode mati + dependensi tak terpakai (lihat §2.4)
- **T-051** Hapus jejak Gemini/AI build: `metadata.json`, `.env.example`, dependensi Firebase AI/AppCheck, plugin `secrets`/`google-services`
- **T-052** Samakan versi & perbaiki klaim README/ARSITEKTUR agar sesuai kode
- **T-053** Rapikan warna tajwid: kode jadi sumber kebenaran, dokumen disamakan

### Fase 5 — Rilis  `final`
- **T-060** Tag `v2.0.0`, pastikan rilis otomatis terbit dengan APK
- **T-061** Bersihkan token GitHub dari URL remote, minta user revoke
- **T-062** Laporan akhir: daftar ticket selesai + bukti Actions hijau

---

## 4. Ruang Lingkup & Batasan

**Masuk:** perbaikan bug, upgrade visual 3D/animasi, aksesibilitas, cleanup, CI/CD.
**Keluar (sengaja):** menambah data Al-Qur'an penuh (6.236 ayat) — butuh sumber data berlisensi
terpisah; ini dicatat sebagai risiko, bukan ticket.

**Ponytail aktif:** setiap perubahan memakai tangga *sudah ada di kode → stdlib → native →
dependensi terpasang → minimal*. Tanpa abstraksi baru yang tidak diminta.
