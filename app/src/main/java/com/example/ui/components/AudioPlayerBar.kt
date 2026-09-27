package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.util.AudioPlaybackState
import java.util.Locale

@Composable
fun AudioPlayerBar(
    state: AudioPlaybackState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onCycleRepeat: () -> Unit,
    onClose: () -> Unit,
    onCycleSpeed: () -> Unit = {},
    onSetSleepTimer: (Int?) -> Unit = {},
    onDownload: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isVisible = state.isPlaying || state.isLoading || state.isDownloading || state.currentPositionMs > 0

    var showSleepTimerMenu by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("audio_player_bar"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tier 1: Surah Title, Qari, and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = EmeraldMint,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "${state.currentSurahName} : Ayat ${state.currentVerseNumber}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = state.selectedQari.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (state.sleepTimerMinutesRemaining != null) {
                                    Text(
                                        text = " • ⏱️ ${state.sleepTimerMinutesRemaining}m",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldAccent
                                    )
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = onDownload,
                        enabled = !state.isDownloading,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Unduh surah ini untuk dibaca offline",
                            tint = if (state.downloadStatus != null) GoldAccent
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup Pemutar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (state.downloadStatus != null) {
                    Text(
                        text = state.downloadStatus.orEmpty(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldAccent
                    )
                }

                // Tier 2: Progress Scrubber
                val progress = if (state.durationMs > 0) {
                    (state.currentPositionMs.toFloat() / state.durationMs.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(state.currentPositionMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (state.durationMs > 0) formatTime(state.durationMs) else "--:--",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Tier 3: Controls (Interactive Chips on Left, Playback Buttons on Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Feature Pills (Speed, Timer, Repeat)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Speed Chip (min 44dp target)
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onCycleSpeed() }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${state.playbackSpeed}x",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldMint
                            )
                        }

                        // Sleep Timer Button with Dropdown
                        Box {
                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (state.sleepTimerMinutesRemaining != null) GoldAccent.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { showSleepTimerMenu = true }
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Pengatur Waktu Tidur",
                                        tint = if (state.sleepTimerMinutesRemaining != null) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    if (state.sleepTimerMinutesRemaining != null) {
                                        Text(
                                            text = "${state.sleepTimerMinutesRemaining}m",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = showSleepTimerMenu,
                                onDismissRequest = { showSleepTimerMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Mati (Off)") },
                                    onClick = {
                                        onSetSleepTimer(null)
                                        showSleepTimerMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("15 Menit") },
                                    onClick = {
                                        onSetSleepTimer(15)
                                        showSleepTimerMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("30 Menit") },
                                    onClick = {
                                        onSetSleepTimer(30)
                                        showSleepTimerMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("45 Menit") },
                                    onClick = {
                                        onSetSleepTimer(45)
                                        showSleepTimerMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("60 Menit") },
                                    onClick = {
                                        onSetSleepTimer(60)
                                        showSleepTimerMenu = false
                                    }
                                )
                            }
                        }

                        // Repeat / Loop badge button (Tahfidz)
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { onCycleRepeat() }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = "Ulangi Hafalan",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${state.targetRepeatCount}x" +
                                            if (state.targetRepeatCount > 1) " (${state.currentRepeatIteration})" else "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }

                    // Playback Controls (Prev, Big Play/Pause, Next)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = onPrev,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Ayat Sebelumnya",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Prominent Primary Action Button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                                .clickable { onPlayPause() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (state.isPlaying) "Jeda" else "Putar",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onNext,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Ayat Berikutnya",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Int): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
