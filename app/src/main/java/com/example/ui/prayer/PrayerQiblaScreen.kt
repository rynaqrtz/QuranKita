package com.example.ui.prayer

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.util.PrayerTimeCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerQiblaScreen(
    viewModel: PrayerQiblaViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val compassState by viewModel.compassState.collectAsStateWithLifecycle()

    var showCityDropdown by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        viewModel.onResume()
        onDispose {
            viewModel.onPause()
        }
    }

    LaunchedEffect(compassState.isAlignedWithQibla) {
        viewModel.checkAndTriggerHaptic(compassState.isAlignedWithQibla)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ibadah & Waktu",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    )
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
        ) {
            // Tab Selector
            PrimaryTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = EmeraldPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == PrayerQiblaTab.SHOLAT,
                    onClick = { viewModel.setTab(PrayerQiblaTab.SHOLAT) },
                    text = { Text("Jadwal Sholat", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == PrayerQiblaTab.KIBLAT,
                    onClick = { viewModel.setTab(PrayerQiblaTab.KIBLAT) },
                    text = { Text("Arah Kiblat", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                PrayerQiblaTab.SHOLAT -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Location & City Selector Header
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { showCityDropdown = true },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldPrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "Lokasi: ${selectedCity.name}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Metode Kemenag RI (+2 mnt Ikhtiyat) • 100% Offline",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Pilih Kota"
                                    )
                                }

                                DropdownMenu(
                                    expanded = showCityDropdown,
                                    onDismissRequest = { showCityDropdown = false }
                                ) {
                                    PrayerTimeCalculator.DEFAULT_CITIES.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city.name) },
                                            onClick = {
                                                viewModel.selectCity(city)
                                                showCityDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Prayer Times Cards
                        items(prayerTimes, key = { it.name }) { prayer ->
                            val isNext = prayer.isNext
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isNext) EmeraldPrimary else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isNext) 4.dp else 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isNext) Color.White.copy(alpha = 0.2f)
                                                    else MaterialTheme.colorScheme.primaryContainer
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.NotificationsActive,
                                                contentDescription = null,
                                                tint = if (isNext) Color.White else EmeraldPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = prayer.name,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isNext) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            if (isNext && prayer.remainingTimeText.isNotEmpty()) {
                                                Text(
                                                    text = prayer.remainingTimeText,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = GoldLight,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "${prayer.time} WIB",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp,
                                            color = if (isNext) Color.White else EmeraldPrimary
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                PrayerQiblaTab.KIBLAT -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Location & Degree info card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Arah Ka'bah dari ${selectedCity.name}:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${compassState.qiblaBearing.toInt()}° Barat Laut",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Jarak ke Makkah:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "± ${compassState.distanceToMakkahKm} km",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                    )
                                }
                            }
                        }

                        // Alignment Status Banner
                        if (compassState.isAlignedWithQibla) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(EmeraldPrimary)
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text(
                                        text = "Alhamdulillah! Tepat Menghadap Kiblat",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Putar ponsel hingga jarum emas sejajar dengan simbol Ka'bah di atas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Compass Graphic
                        QiblaCompassGraphic(
                            deviceAzimuth = compassState.deviceAzimuth,
                            qiblaBearing = compassState.qiblaBearing,
                            isAligned = compassState.isAlignedWithQibla,
                            modifier = Modifier
                                .size(280.dp)
                                .testTag("qibla_compass_dial")
                        )

                        // Calibration note
                        Text(
                            text = "💡 Tips: Jika kompas tidak stabil, gerakkan ponsel membentuk angka 8 di udara untuk kalibrasi sensor.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QiblaCompassGraphic(
    deviceAzimuth: Float,
    qiblaBearing: Float,
    isAligned: Boolean,
    modifier: Modifier = Modifier
) {
    val dialRotation = -deviceAzimuth
    val qiblaRelative = (qiblaBearing - deviceAzimuth + 360f) % 360f

    val primaryColor = if (isAligned) EmeraldPrimary else Color(0xFF334155)
    val goldColor = GoldAccent

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Dial Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 20.dp.toPx()

            // Outer ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.2f),
                radius = radius + 12.dp.toPx(),
                style = Stroke(width = 3.dp.toPx())
            )

            // Inner circle dial
            drawCircle(
                color = Color(0xFF1E293B).copy(alpha = 0.5f),
                radius = radius
            )

            // Tick marks on dial
            rotate(dialRotation, pivot = center) {
                for (deg in 0 until 360 step 15) {
                    val isMajor = deg % 90 == 0
                    val tickLen = if (isMajor) 16.dp.toPx() else 8.dp.toPx()
                    val tickColor = if (deg == 0) Color(0xFFEF4444) // North in Red
                    else if (isMajor) Color.White.copy(alpha = 0.8f)
                    else Color.White.copy(alpha = 0.3f)

                    val angleRad = Math.toRadians(deg.toDouble())
                    val startX = center.x + (radius - tickLen) * kotlin.math.sin(angleRad).toFloat()
                    val startY = center.y - (radius - tickLen) * kotlin.math.cos(angleRad).toFloat()
                    val endX = center.x + radius * kotlin.math.sin(angleRad).toFloat()
                    val endY = center.y - radius * kotlin.math.cos(angleRad).toFloat()

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isMajor) 2.5.dp.toPx() else 1.2.dp.toPx()
                    )
                }

                // Qibla needle pointer
                rotate(qiblaBearing, pivot = center) {
                    val needlePath = Path().apply {
                        moveTo(center.x, center.y - radius + 14.dp.toPx())
                        lineTo(center.x - 10.dp.toPx(), center.y)
                        lineTo(center.x + 10.dp.toPx(), center.y)
                        close()
                    }
                    drawPath(
                        path = needlePath,
                        color = goldColor
                    )
                }
            }

            // Center jewel
            drawCircle(
                color = if (isAligned) EmeraldPrimary else goldColor,
                radius = 12.dp.toPx()
            )
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx()
            )
        }

        // Center Ka'bah Icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isAligned) EmeraldPrimary else Color(0xFF0F172A))
                .border(2.dp, goldColor, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🕋",
                fontSize = 18.sp
            )
        }
    }
}
