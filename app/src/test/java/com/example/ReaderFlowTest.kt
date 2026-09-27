package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.QuranRepository
import com.example.ui.reader.SurahReaderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReaderFlowTest {

    private lateinit var app: Application
    private lateinit var repository: QuranRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        app = ApplicationProvider.getApplicationContext()
        repository = QuranRepository(AppDatabase.getInstance(app).quranDao())
        runBlocking { repository.initializeDatabase(app) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun <T> awaitState(
        flow: kotlinx.coroutines.flow.StateFlow<T>,
        timeoutMs: Long = 20_000,
        predicate: (T) -> Boolean
    ): T {
        val deadline = System.currentTimeMillis() + timeoutMs
        var latest = flow.value
        while (System.currentTimeMillis() < deadline) {
            if (predicate(latest)) return latest
            Thread.sleep(50)
            latest = flow.value
        }
        return latest
    }

    private class Subscribers(val scope: CoroutineScope)

    private fun subscribeTo(vm: SurahReaderViewModel): Subscribers {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        scope.launch { vm.currentSurah.collect {} }
        scope.launch { vm.verses.collect {} }
        return Subscribers(scope)
    }

    @Test
    fun readerFollowsActiveSurah() {
        val vm = SurahReaderViewModel(app)
        val subscribers = subscribeTo(vm)
        try {
            vm.loadSurah(2)
            val surah2 = awaitState(vm.currentSurah) { it?.number == 2 }
            assertNotNull("reader never resolved surah 2", surah2)
            assertEquals("Al-Baqarah", surah2?.nameLatin)
            assertEquals(286, surah2?.verseCount)

            val verses2 = awaitState(vm.verses) { it.size == 286 }
            assertEquals(286, verses2.size)
            assertEquals(1, verses2.first().verseNumber)
            assertEquals(286, verses2.last().verseNumber)

            vm.loadSurah(1)
            val surah1 = awaitState(vm.currentSurah) { it?.number == 1 }
            assertEquals("Al-Fatihah", surah1?.nameLatin)
            assertEquals(7, awaitState(vm.verses) { it.size == 7 }.size)
        } finally {
            subscribers.scope.cancel()
        }
    }

    @Test
    fun loadSurahDoesNotOverwriteLastRead() {
        runBlocking {
            repository.setLastRead(5, "Al-Ma'idah", 20)

            val vm = SurahReaderViewModel(app)
            val subscribers = subscribeTo(vm)
            try {
                vm.loadSurah(5)
                assertEquals(5, awaitState(vm.currentSurah) { it?.number == 5 }?.number)

                Thread.sleep(1_500)

                val lastRead = repository.lastRead.first()
                assertNotNull(lastRead)
                assertEquals(5, lastRead?.surahNumber)
                assertEquals("Al-Ma'idah", lastRead?.surahName)
                assertEquals(20, lastRead?.verseNumber)
            } finally {
                subscribers.scope.cancel()
            }
        }
    }

    @Test
    fun bundledQuranDataIsComplete() {
        runBlocking {
            val surahs = repository.allSurahs.first()
            assertEquals(114, surahs.size)

            var total = 0
            for (surah in surahs) {
                val verses = repository.getVersesForSurah(surah.number).first()
                assertEquals(
                    "surah ${surah.number} (${surah.nameLatin}) verse count mismatch",
                    surah.verseCount,
                    verses.size
                )
                assertTrue(
                    "surah ${surah.number} has blank arabic text",
                    verses.all { it.arabic.isNotBlank() }
                )
                total += verses.size
            }
            assertEquals(6236, total)
        }
    }
}
