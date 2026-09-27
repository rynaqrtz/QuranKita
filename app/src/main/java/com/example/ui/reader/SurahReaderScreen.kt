package com.example.ui.reader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Qari
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.ui.components.AudioPlayerBar
import com.example.ui.navigation.LocalNavAnimatedVisibilityScope
import com.example.ui.navigation.LocalSharedTransitionScope
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldVibrant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.QuranTypoPresets
import com.example.util.TajwidFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SurahReaderScreen(
    surahNumber: Int,
    viewModel: SurahReaderViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current

    val badgeModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                rememberSharedContentState(key = "surah_badge_$surahNumber"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else Modifier

    BackHandler(onBack = onNavigateBack)

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    val currentSurah by viewModel.currentSurah.collectAsStateWithLifecycle()
    val verses by viewModel.verses.collectAsStateWithLifecycle()
    val settings by viewModel.readerSettings.collectAsStateWithLifecycle()
    val revealedVerses by viewModel.revealedVerses.collectAsStateWithLifecycle()
    val expandedTafsir by viewModel.expandedTafsir.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()

    var showSettingsSheet by remember { mutableStateOf(false) }
    var selectedTafsirVerse by remember { mutableStateOf<Verse?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val tafsirSheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Auto-update last read when scrolling
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    LaunchedEffect(firstVisibleItemIndex) {
        if (verses.isNotEmpty() && firstVisibleItemIndex in verses.indices) {
            val v = verses[firstVisibleItemIndex]
            currentSurah?.let {
                viewModel.updateLastRead(v.verseNumber, it.nameLatin)
            }
        }
    }

    val currentVersePos = if (verses.isNotEmpty() && firstVisibleItemIndex in verses.indices) firstVisibleItemIndex + 1 else 1
    val readingProgressFraction = if (verses.isNotEmpty()) (currentVersePos.toFloat() / verses.size.toFloat()).coerceIn(0f, 1f) else 0f

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .then(badgeModifier)
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(EmeraldDark, Color(0xFF0F766E))
                                    )
                                )
                                .border(1.dp, EmeraldMint.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = surahNumber.toString(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldMint
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = currentSurah?.nameLatin ?: "Surah $surahNumber",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${currentSurah?.meaning ?: ""} • ${currentSurah?.verseCount ?: 0} Ayat",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("reader_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    // Play Surah Audio Button
                    currentSurah?.let { surah ->
                        IconButton(
                            onClick = { viewModel.playAll(surah) },
                            modifier = Modifier.testTag("reader_play_all_button")
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying && playbackState.currentSurahNumber == surah.number) {
                                    Icons.Default.Pause
                                } else {
                                    Icons.Default.PlayArrow
                                },
                                contentDescription = "Putar Murottal Surah",
                                tint = EmeraldPrimary
                            )
                        }
                    }

                    // Settings Button
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("reader_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Pengaturan Baca"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            AudioPlayerBar(
                state = playbackState,
                onPlayPause = {
                    if (playbackState.isPlaying) viewModel.audioPlayer.pause()
                    else viewModel.audioPlayer.resume()
                },
                onNext = { viewModel.audioPlayer.playNext() },
                onPrev = { viewModel.audioPlayer.playPrevious() },
                onCycleRepeat = { viewModel.cycleRepeatCount() },
                onClose = { viewModel.audioPlayer.stop() },
                onCycleSpeed = {
                    val currentSpeed = playbackState.playbackSpeed
                    val nextSpeed = when (currentSpeed) {
                        0.75f -> 1.0f
                        1.0f -> 1.25f
                        1.25f -> 1.5f
                        else -> 0.75f
                    }
                    viewModel.audioPlayer.setPlaybackSpeed(nextSpeed)
                },
                onSetSleepTimer = { minutes ->
                    viewModel.audioPlayer.setSleepTimer(minutes)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Reading Progress Bar in Surah
            LinearProgressIndicator(
                progress = { readingProgressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // View Mode Selector Chip Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = settings.viewMode == ReadingViewMode.PER_AYAT,
                        onClick = { viewModel.setViewMode(ReadingViewMode.PER_AYAT) },
                        label = { Text("Per-Ayat", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = EmeraldPrimary
                        )
                    )
                    FilterChip(
                        selected = settings.viewMode == ReadingViewMode.MUSHAF,
                        onClick = { viewModel.setViewMode(ReadingViewMode.MUSHAF) },
                        label = { Text("Mushaf", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = EmeraldPrimary
                        )
                    )
                }

                // Quick Tahfidz Hide Mode Toggle
                FilterChip(
                    selected = settings.isTahfidzHideMode,
                    onClick = { viewModel.toggleTahfidzHideMode() },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (settings.isTahfidzHideMode) "Uji Hafalan On" else "Uji Hafalan",
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAccent.copy(alpha = 0.2f),
                        selectedLabelColor = GoldAccent
                    )
                )
            }

            if (settings.viewMode == ReadingViewMode.PER_AYAT) {
                // Mode Per Ayat: List of detailed cards
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Basmalah Banner (except At-Taubah #9)
                    if (surahNumber != 9) {
                        item {
                            BasmalahBanner()
                        }
                    }

                    items(verses, key = { it.id }) { verse ->
                        val isPlayingThisVerse = playbackState.isPlaying &&
                                playbackState.currentSurahNumber == verse.surahNumber &&
                                playbackState.currentVerseNumber == verse.verseNumber

                        val isHiddenInTahfidz = settings.isTahfidzHideMode && !revealedVerses.contains(verse.id)
                        val isTafsirOpen = expandedTafsir.contains(verse.id)

                        VerseCardItem(
                            verse = verse,
                            totalVerses = currentSurah?.verseCount ?: 7,
                            surahName = currentSurah?.nameLatin ?: "Surah",
                            arabicFontSize = settings.arabicFontSize,
                            isTajwidEnabled = settings.isTajwidEnabled,
                            isTransliterationEnabled = settings.isTransliterationEnabled,
                            isHiddenInTahfidz = isHiddenInTahfidz,
                            isPlaying = isPlayingThisVerse,
                            isTafsirExpanded = isTafsirOpen,
                            onPlay = {
                                viewModel.playVerse(
                                    verse = verse,
                                    totalVerses = currentSurah?.verseCount ?: 7,
                                    surahName = currentSurah?.nameLatin ?: "Surah"
                                )
                            },
                            onToggleBookmark = { viewModel.toggleVerseBookmark(verse) },
                            onToggleMemorized = { viewModel.toggleVerseMemorized(verse) },
                            onToggleTafsir = { viewModel.toggleExpandTafsir(verse.id) },
                            onOpenFullTafsir = { selectedTafsirVerse = verse },
                            onRevealVerse = { viewModel.toggleRevealVerse(verse.id) }
                        )
                    }
                }
            } else {
                // Mode Mushaf Kontinu
                MushafContinuousView(
                    verses = verses,
                    arabicFontSize = settings.arabicFontSize,
                    isTajwidEnabled = settings.isTajwidEnabled,
                    surahNumber = surahNumber,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Dedicated Tafsir Detail Bottom Sheet Modal
        selectedTafsirVerse?.let { verse ->
            ModalBottomSheet(
                onDismissRequest = { selectedTafsirVerse = null },
                sheetState = tafsirSheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                TafsirDetailView(
                    surahName = currentSurah?.nameLatin ?: "Surah",
                    verse = verse,
                    onClose = { selectedTafsirVerse = null },
                    onCopy = {
                        val clipText = "QS ${currentSurah?.nameLatin} : Ayat ${verse.verseNumber}\n\n" +
                                "${verse.arabic}\n\n" +
                                "Artinya:\n\"${verse.translation}\"\n\n" +
                                "Tafsir Ringkas:\n${verse.tafsir}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Tafsir Ayat", clipText))
                        Toast.makeText(context, "Tafsir berhasil disalin!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Reader Settings Bottom Sheet
        if (showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSettingsSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Pengaturan Membaca",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )

                    // Font Size Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ukuran Huruf Arab",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "${settings.arabicFontSize.toInt()} sp",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            )
                        }
                        Slider(
                            value = settings.arabicFontSize,
                            onValueChange = viewModel::setArabicFontSize,
                            valueRange = 20f..38f,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldPrimary,
                                activeTrackColor = EmeraldPrimary
                            )
                        )
                    }

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tajwid Berwarna",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Tandai hukum tajwid (ghunnah, ikhfa, mad)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.isTajwidEnabled,
                            onCheckedChange = { viewModel.toggleTajwid() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Teks Transliterasi Latin",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Tampilkan cara baca ejaan latin",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.isTransliterationEnabled,
                            onCheckedChange = { viewModel.toggleTransliteration() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    // Qari Selection
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Pilih Qari Murottal",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Qari.ALL_QARIS.forEach { qari ->
                            val isSelected = playbackState.selectedQari.id == qari.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setQari(qari) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = qari.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = qari.style,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Dipilih",
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun BasmalahBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EmeraldDark.copy(alpha = 0.4f))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            fontFamily = AmiriFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = EmeraldMint
        )
    }
}

@Composable
fun VerseCardItem(
    verse: Verse,
    totalVerses: Int,
    surahName: String,
    arabicFontSize: Float,
    isTajwidEnabled: Boolean,
    isTransliterationEnabled: Boolean,
    isHiddenInTahfidz: Boolean,
    isPlaying: Boolean,
    isTafsirExpanded: Boolean,
    onPlay: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleMemorized: () -> Unit,
    onToggleTafsir: () -> Unit,
    onOpenFullTafsir: () -> Unit,
    onRevealVerse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("verse_card_${verse.verseNumber}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) EmeraldDark.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Verse Header: Number Badge and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle Number Badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPlaying) EmeraldPrimary else MaterialTheme.colorScheme.surface
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = verse.verseNumber.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isPlaying) Color.White else EmeraldPrimary
                        )
                    )
                }

                // Action Buttons Row (Accessible Min 42dp Touch Target)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Full Tafsir Modal Button
                    IconButton(
                        onClick = onOpenFullTafsir,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Buka Tafsir Lengkap",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Mark as Memorized (Tahfidz)
                    IconButton(
                        onClick = onToggleMemorized,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = if (verse.isMemorized) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                            contentDescription = "Tandai Hafalan",
                            tint = if (verse.isMemorized) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bookmark
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = if (verse.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark Ayat",
                            tint = if (verse.isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Play Audio
                    IconButton(
                        onClick = onPlay,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Putar Audio Ayat",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Arabic Text (or Tahfidz hidden mode)
            if (isHiddenInTahfidz) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        .clickable { onRevealVerse() }
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Mode Uji Hafalan Aktif",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldAccent
                        )
                        Text(
                            text = "Ketuk di sini untuk membuka teks ayat",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val formattedArabic = remember(verse.arabic, isTajwidEnabled) {
                    TajwidFormatter.formatWithTajwid(verse.arabic, isTajwidEnabled)
                }

                Text(
                    text = formattedArabic,
                    fontFamily = AmiriFontFamily,
                    fontSize = arabicFontSize.sp,
                    lineHeight = (arabicFontSize * 1.85f).sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Transliteration
            if (isTransliterationEnabled && verse.transliteration.isNotEmpty()) {
                Text(
                    text = verse.transliteration,
                    style = QuranTypoPresets.transliteration,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Translation (Kemenag)
            Text(
                text = verse.translation,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    lineHeight = 23.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.92f)
            )

            // Inline Tafsir Quick Accordion
            if (verse.tafsir.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleTafsir() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Tafsir Ringkas",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        )
                        Icon(
                            imageVector = if (isTafsirExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Buka Tafsir",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    AnimatedVisibility(visible = isTafsirExpanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = verse.tafsir,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dedicated, Rich Tafsir Detail View Modal Sheet
 */
@Composable
fun TafsirDetailView(
    surahName: String,
    verse: Verse,
    onClose: () -> Unit,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sheet Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tafsir Ayat",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
                Text(
                    text = "QS. $surahName : Ayat ${verse.verseNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
            }
        }

        // Arabic text card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = verse.arabic,
                fontFamily = AmiriFontFamily,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    lineHeight = 42.sp,
                    textAlign = TextAlign.End
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Translation Section
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Terjemahan (Kemenag RI):",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            )
            Text(
                text = "\"${verse.translation}\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Tafsir Content Section
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Tafsir Ringkas & Konteks Kandungan:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            )
            Text(
                text = if (verse.tafsir.isNotEmpty()) verse.tafsir
                else "Tafsir ringkas ayat ini menjelaskan tentang pokok-pokok petunjuk, ketakwaan kepada Allah SWT, dan hikmah berharga bagi kaum yang berfikir.",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Action Buttons: Copy
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onCopy,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salin Tafsir", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MushafContinuousView(
    verses: List<Verse>,
    arabicFontSize: Float,
    isTajwidEnabled: Boolean,
    surahNumber: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (surahNumber != 9) {
            item {
                BasmalahBanner()
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    verses.forEach { verse ->
                        val formatted = remember(verse.arabic, isTajwidEnabled) {
                            TajwidFormatter.formatWithTajwid(verse.arabic, isTajwidEnabled)
                        }
                        Text(
                            text = "$formatted  ۝${verse.verseNumber}  ",
                            fontFamily = AmiriFontFamily,
                            fontSize = arabicFontSize.sp,
                            lineHeight = (arabicFontSize * 1.85f).sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
