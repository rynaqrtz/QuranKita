package com.example.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestion
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranQuizScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateBack)
    val quizBank = remember {
        listOf(
            QuizQuestion(
                surahNumber = 1,
                surahName = "Al-Fatihah",
                verseNumber = 1,
                promptArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                promptTranslation = "Apa bunyi ayat ke-2 dari Surah Al-Fatihah?",
                optionsArabic = listOf(
                    "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    "الرَّحْمَٰنِ الرَّحِيمِ",
                    "مَالِكِ يَوْمِ الدِّينِ",
                    "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ"
                ),
                correctIndex = 0,
                explanation = "Ayat ke-2 adalah: 'Al-hamdu lillahi rabbil-'alamin' (Segala puji bagi Allah, Tuhan seluruh alam)."
            ),
            QuizQuestion(
                surahNumber = 112,
                surahName = "Al-Ikhlas",
                verseNumber = 1,
                promptArabic = "قُلْ هُوَ اللَّهُ أَحَدٌ",
                promptTranslation = "Lanjutan ayat ke-2 dari Surah Al-Ikhlas adalah...",
                optionsArabic = listOf(
                    "اللَّهُ الصَّمَدُ",
                    "لَمْ يَلِدْ وَلَمْ يُولَدْ",
                    "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
                    "مِن شَرِّ مَا خَلَقَ"
                ),
                correctIndex = 0,
                explanation = "Ayat ke-2 adalah: 'Allahus-Samad' (Allah tempat meminta segala sesuatu)."
            ),
            QuizQuestion(
                surahNumber = 114,
                surahName = "An-Nas",
                verseNumber = 1,
                promptArabic = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                promptTranslation = "Lanjutan ayat ke-2 dari Surah An-Nas adalah...",
                optionsArabic = listOf(
                    "مَلِكِ النَّاسِ",
                    "إِلَٰهِ النَّاسِ",
                    "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ",
                    "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ"
                ),
                correctIndex = 0,
                explanation = "Ayat ke-2 adalah: 'Malikin-Nas' (Raja manusia)."
            ),
            QuizQuestion(
                surahNumber = 108,
                surahName = "Al-Kausar",
                verseNumber = 1,
                promptArabic = "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ",
                promptTranslation = "Lanjutan ayat ke-2 dari Surah Al-Kausar adalah...",
                optionsArabic = listOf(
                    "فَصَلِّ لِرَبِّكَ وَانْحَرْ",
                    "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ",
                    "وَرَأَيْتَ النَّاسَ يَدْخُلُونَ",
                    "فَسَبِّحْ بِحَمْدِ رَبِّكَ"
                ),
                correctIndex = 0,
                explanation = "Ayat ke-2 adalah: 'Fa shalli li rabbika wan-har' (Maka laksanakanlah sholat karena Tuhanmu, dan berkurbanlah)."
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var isQuizCompleted by remember { mutableStateOf(false) }

    val currentQuestion = quizBank[currentIndex]

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Kuis Hafalan & Tajwid",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isQuizCompleted) {
                // Quiz Completed Result Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF080D16),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "Alhamdulillah!",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldMint
                        )

                        Text(
                            text = "Skor Anda: $score / ${quizBank.size * 100}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )
                        )

                        Text(
                            text = "Terus tingkatkan hafalan dan pemahaman ayat-ayat suci Al-Qur'an.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = {
                                currentIndex = 0
                                selectedOptionIndex = null
                                isAnswerSubmitted = false
                                score = 0
                                isQuizCompleted = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Ulangi Kuis", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Question Progress Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Soal ${currentIndex + 1} dari ${quizBank.size}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    )

                    Text(
                        text = "Skor: $score",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    )
                }

                // Prompt Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "QS. ${currentQuestion.surahName} [${currentQuestion.surahNumber}]",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        )

                        Text(
                            text = currentQuestion.promptArabic,
                            fontFamily = AmiriFontFamily,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 24.sp,
                                textAlign = TextAlign.End,
                                lineHeight = 38.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = currentQuestion.promptTranslation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Options List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentQuestion.optionsArabic.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrect = index == currentQuestion.correctIndex

                        val containerColor = when {
                            !isAnswerSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                            isAnswerSubmitted && isCorrect -> EmeraldDark
                            isAnswerSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                            else -> DarkCardSurface
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isAnswerSubmitted) {
                                    selectedOptionIndex = index
                                }
                                .testTag("quiz_option_$index"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = containerColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )

                                if (isAnswerSubmitted) {
                                    if (isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Benar",
                                            tint = EmeraldMint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Salah",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Explanation on submission
                AnimatedVisibility(visible = isAnswerSubmitted) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = currentQuestion.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Button
                if (!isAnswerSubmitted) {
                    Button(
                        onClick = {
                            if (selectedOptionIndex != null) {
                                isAnswerSubmitted = true
                                if (selectedOptionIndex == currentQuestion.correctIndex) {
                                    score += 100
                                }
                            }
                        },
                        enabled = selectedOptionIndex != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Periksa Jawaban", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            if (currentIndex < quizBank.size - 1) {
                                currentIndex++
                                selectedOptionIndex = null
                                isAnswerSubmitted = false
                            } else {
                                isQuizCompleted = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(
                            text = if (currentIndex < quizBank.size - 1) "Soal Berikutnya" else "Lihat Hasil Akhir",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
