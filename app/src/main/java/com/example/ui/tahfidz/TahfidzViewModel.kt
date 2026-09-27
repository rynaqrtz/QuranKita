package com.example.ui.tahfidz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.QuizQuestion
import com.example.data.model.Verse
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizState(
    val currentQuestionIndex: Int = 0,
    val questions: List<QuizQuestion> = emptyList(),
    val selectedOptionIndex: Int? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val isQuizFinished: Boolean = false
)

class TahfidzViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository

    val memorizedVerses: StateFlow<List<Verse>>

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuranRepository(db.quranDao())

        memorizedVerses = repository.memorizedVerses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        generateQuizQuestions()
    }

    private fun generateQuizQuestions() {
        // Preset high-yield quiz questions from popular surahs (Al-Fatihah, Al-Ikhlas, Al-Falaq, An-Nas, Al-Mulk, Al-Asr)
        val list = listOf(
            QuizQuestion(
                surahNumber = 1,
                surahName = "Al-Fatihah",
                verseNumber = 1,
                promptArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                promptTranslation = "Lanjutan dari ayat pertama Al-Fatihah adalah...",
                optionsArabic = listOf(
                    "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    "مَالِكِ يَوْمِ الدِّينِ",
                    "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ"
                ),
                correctIndex = 0
            ),
            QuizQuestion(
                surahNumber = 112,
                surahName = "Al-Ikhlas",
                verseNumber = 1,
                promptArabic = "قُلْ هُوَ اللَّهُ أَحَدٌ",
                promptTranslation = "Ayat berikutnya dari Surah Al-Ikhlas adalah...",
                optionsArabic = listOf(
                    "لَمْ يَلِدْ وَلَمْ يُولَدْ",
                    "اللَّهُ الصَّمَدُ",
                    "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ"
                ),
                correctIndex = 1
            ),
            QuizQuestion(
                surahNumber = 103,
                surahName = "Al-'Asr",
                verseNumber = 1,
                promptArabic = "وَالْعَصْرِ",
                promptTranslation = "Ayat kedua dari Surah Al-'Asr adalah...",
                optionsArabic = listOf(
                    "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ",
                    "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ",
                    "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ"
                ),
                correctIndex = 2
            ),
            QuizQuestion(
                surahNumber = 114,
                surahName = "An-Nas",
                verseNumber = 1,
                promptArabic = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                promptTranslation = "Ayat kedua dari Surah An-Nas adalah...",
                optionsArabic = listOf(
                    "مَلِكِ النَّاسِ",
                    "إِلَٰهِ النَّاسِ",
                    "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ"
                ),
                correctIndex = 0
            ),
            QuizQuestion(
                surahNumber = 67,
                surahName = "Al-Mulk",
                verseNumber = 1,
                promptArabic = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                promptTranslation = "Ayat kedua dari Surah Al-Mulk adalah...",
                optionsArabic = listOf(
                    "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا",
                    "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا",
                    "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ"
                ),
                correctIndex = 0
            )
        )

        _quizState.value = QuizState(
            questions = list,
            currentQuestionIndex = 0,
            score = 0
        )
    }

    fun submitAnswer(optionIndex: Int) {
        val state = _quizState.value
        if (state.isAnswered) return

        val question = state.questions.getOrNull(state.currentQuestionIndex) ?: return
        val isCorrect = optionIndex == question.correctIndex

        _quizState.value = state.copy(
            selectedOptionIndex = optionIndex,
            isAnswered = true,
            isCorrect = isCorrect,
            score = if (isCorrect) state.score + 20 else state.score
        )
    }

    fun nextQuestion() {
        val state = _quizState.value
        if (state.currentQuestionIndex + 1 < state.questions.size) {
            _quizState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex + 1,
                selectedOptionIndex = null,
                isAnswered = false,
                isCorrect = false
            )
        } else {
            _quizState.value = state.copy(isQuizFinished = true)
        }
    }

    fun restartQuiz() {
        generateQuizQuestions()
    }

    fun unmarkMemorized(verse: Verse) {
        viewModelScope.launch {
            repository.toggleVerseMemorized(verse.id, true)
        }
    }
}
