package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent

/**
 * Modern minimalist Quran app logo icon badge with soothing emerald, celestial gold, and sky cyan.
 */
@Composable
fun QuranAppLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    Image(
        painter = painterResource(id = R.drawable.quran_logo_clean),
        contentDescription = "QuranKita Logo",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
    )
}

@Composable
fun TixarLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    QuranAppLogoBadge(
        modifier = modifier,
        size = size
    )
}

/**
 * Clean Top Bar Header with the new modern Quran logo and elegant branding.
 */
@Composable
fun TixarBrandedHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuranAppLogoBadge(size = 30.dp)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Quran",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Kita",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp
                ),
                color = EmeraldGreen
            )
            Text(
                text = " ✨",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent
            )
        }
    }
}
