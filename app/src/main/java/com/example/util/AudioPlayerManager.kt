package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import com.example.data.model.Qari
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurahNumber: Int = 1,
    val currentSurahName: String = "Al-Fatihah",
    val currentVerseNumber: Int = 1,
    val totalVersesInSurah: Int = 7,
    val selectedQari: Qari = Qari.ALL_QARIS.first(),
    val targetRepeatCount: Int = 1, // 1x, 3x, 5x, 10x
    val currentRepeatIteration: Int = 1,
    val playbackSpeed: Float = 1.0f,
    val sleepTimerMinutesRemaining: Int? = null,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val errorMessage: String? = null
)

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    fun setQari(qari: Qari) {
        _playbackState.value = _playbackState.value.copy(selectedQari = qari)
    }

    fun setTargetRepeatCount(count: Int) {
        _playbackState.value = _playbackState.value.copy(targetRepeatCount = count)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackState.value = _playbackState.value.copy(playbackSpeed = speed)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            try {
                mediaPlayer?.playbackParams = PlaybackParams().apply { this.speed = speed }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            _playbackState.value = _playbackState.value.copy(sleepTimerMinutesRemaining = null)
            return
        }

        _playbackState.value = _playbackState.value.copy(sleepTimerMinutesRemaining = minutes)
        sleepTimerJob = scope.launch {
            var remaining = minutes * 60
            while (isActive && remaining > 0) {
                delay(1000)
                remaining--
                if (remaining % 60 == 0) {
                    _playbackState.value = _playbackState.value.copy(
                        sleepTimerMinutesRemaining = remaining / 60
                    )
                }
            }
            if (isActive) {
                stop()
                _playbackState.value = _playbackState.value.copy(sleepTimerMinutesRemaining = null)
            }
        }
    }

    fun playVerse(
        surahNumber: Int,
        surahName: String,
        verseNumber: Int,
        totalVerses: Int
    ) {
        val state = _playbackState.value
        val isSameVerse = state.currentSurahNumber == surahNumber && state.currentVerseNumber == verseNumber
        if (isSameVerse && mediaPlayer != null) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                _playbackState.value = state.copy(isPlaying = false)
            } else {
                mediaPlayer?.start()
                _playbackState.value = state.copy(isPlaying = true)
                startProgressTracker()
            }
            return
        }

        releasePlayer()

        _playbackState.value = state.copy(
            isLoading = true,
            currentSurahNumber = surahNumber,
            currentSurahName = surahName,
            currentVerseNumber = verseNumber,
            totalVersesInSurah = totalVerses,
            currentRepeatIteration = 1,
            errorMessage = null
        )

        val qari = state.selectedQari
        val sStr = String.format(Locale.US, "%03d", surahNumber)
        val vStr = String.format(Locale.US, "%03d", verseNumber)

        // Check if offline audio exists
        val offlineFile = File(context.filesDir, "audio/${qari.id}_$sStr.mp3")
        val audioUrl = if (offlineFile.exists()) {
            offlineFile.absolutePath
        } else {
            // EveryAyah CDN public audio
            "https://everyayah.com/data/${qari.subfolder}/$sStr$vStr.mp3"
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    _playbackState.value = _playbackState.value.copy(
                        isLoading = false,
                        isPlaying = true,
                        durationMs = mp.duration
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            mp.playbackParams = PlaybackParams().apply { speed = _playbackState.value.playbackSpeed }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mp.start()
                    startProgressTracker()
                }

                setOnCompletionListener {
                    onVerseCompleted()
                }

                setOnErrorListener { _, _, _ ->
                    _playbackState.value = _playbackState.value.copy(
                        isLoading = false,
                        isPlaying = false,
                        errorMessage = "Gagal memutar audio. Periksa koneksi internet."
                    )
                    true
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _playbackState.value = _playbackState.value.copy(
                isLoading = false,
                isPlaying = false,
                errorMessage = e.localizedMessage
            )
        }
    }

    private fun onVerseCompleted() {
        val state = _playbackState.value
        if (state.currentRepeatIteration < state.targetRepeatCount) {
            // Replay same verse
            _playbackState.value = state.copy(
                currentRepeatIteration = state.currentRepeatIteration + 1
            )
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
        } else {
            // Advance to next verse if available
            if (state.currentVerseNumber < state.totalVersesInSurah) {
                playVerse(
                    surahNumber = state.currentSurahNumber,
                    surahName = state.currentSurahName,
                    verseNumber = state.currentVerseNumber + 1,
                    totalVerses = state.totalVersesInSurah
                )
            } else {
                releasePlayer()
            }
        }
    }

    fun playNext() {
        val state = _playbackState.value
        if (state.currentVerseNumber < state.totalVersesInSurah) {
            playVerse(
                surahNumber = state.currentSurahNumber,
                surahName = state.currentSurahName,
                verseNumber = state.currentVerseNumber + 1,
                totalVerses = state.totalVersesInSurah
            )
        }
    }

    fun playPrevious() {
        val state = _playbackState.value
        if (state.currentVerseNumber > 1) {
            playVerse(
                surahNumber = state.currentSurahNumber,
                surahName = state.currentSurahName,
                verseNumber = state.currentVerseNumber - 1,
                totalVerses = state.totalVersesInSurah
            )
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
        progressJob?.cancel()
    }

    fun resume() {
        mediaPlayer?.start()
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
        startProgressTracker()
    }

    fun stop() {
        sleepTimerJob?.cancel()
        _playbackState.value = _playbackState.value.copy(sleepTimerMinutesRemaining = null)
        releasePlayer()
    }

    private fun releasePlayer() {
        progressJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            isLoading = false,
            currentPositionMs = 0
        )
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                try {
                    val pos = mediaPlayer?.currentPosition ?: 0
                    val dur = mediaPlayer?.duration ?: 0
                    _playbackState.value = _playbackState.value.copy(
                        currentPositionMs = pos,
                        durationMs = dur
                    )
                } catch (e: Exception) {
                    // Ignore
                }
                delay(300)
            }
        }
    }
}
