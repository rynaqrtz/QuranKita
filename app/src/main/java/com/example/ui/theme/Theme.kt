package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.R

// ============================================================================
// 1. FONT FAMILIES
// ============================================================================

/**
 * Modern, clean grotesque sans-serif for UI, Headings, Subheadings, and Translations.
 * Loaded from local static font asset: res/font/inter.ttf
 */
val InterFontFamily = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
    Font(R.font.inter, FontWeight.Medium),
    Font(R.font.inter, FontWeight.SemiBold),
    Font(R.font.inter, FontWeight.Bold)
)

/**
 * Authentic classical Naskh Quranic font with optimal diacritics & tashkeel positioning.
 * Loaded from local static font asset: res/font/amiri.ttf
 */
val AmiriFontFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri, FontWeight.Bold)
)

// ============================================================================
// 2. COHESIVE MATERIAL 3 TYPOGRAPHY SYSTEM
// ============================================================================

/**
 * Cohesive typography system with refined type scales:
 * - Headings: Tight letter-spacing, bold prominence, crisp visual hierarchy.
 * - Subheadings: Balanced weights (SemiBold/Medium) providing clear structural grouping.
 * - Body Text: Generous 1.5x–1.6x line-height ratio designed for prolonged, fatigue-free reading.
 * - Labels: Compact, high-legibility micro-typography for buttons, badges, and tabs.
 */
val QuranKitaTypography = Typography(
    // ------------------------------------------------------------------------
    // DISPLAY SCALES (Hero banners, Prayer countdown timer, Khatam stats)
    // ------------------------------------------------------------------------
    displayLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.6).sp
    ),
    displayMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.4).sp
    ),
    displaySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.25).sp
    ),

    // ------------------------------------------------------------------------
    // HEADINGS (Screen titles, Section headers, Dialog & Modal titles)
    // ------------------------------------------------------------------------
    headlineLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.15).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.1).sp
    ),

    // ------------------------------------------------------------------------
    // SUBHEADINGS (Card titles, Group labels, Surah row names, Setting items)
    // ------------------------------------------------------------------------
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = (-0.05).sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.05.sp
    ),

    // ------------------------------------------------------------------------
    // BODY TEXT (Indonesian translations, Tafsir ringkas, Explanatory paragraphs)
    // ------------------------------------------------------------------------
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 25.sp, // ~1.56x line-height ratio for optimal reading flow
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp, // ~1.5x line-height ratio for clean density
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp, // ~1.42x line-height ratio for crisp annotations
        letterSpacing = 0.25.sp
    ),

    // ------------------------------------------------------------------------
    // LABELS / CAPTIONS (Navigation bars, Action chips, Badges, Button labels)
    // ------------------------------------------------------------------------
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.25.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.3.sp
    )
)

// Alias for standard Material 3 access
val Typography = QuranKitaTypography

// ============================================================================
// 3. SPECIALIZED QURANIC READING TYPOGRAPHY HELPERS
// ============================================================================

/**
 * Dedicated typography presets for Quranic reading components to guarantee
 * premium typographic rhythm, proper diacritic breathing space, and high legibility.
 */
@Immutable
object QuranTypoPresets {

    /**
     * Arabic verse text using authentic Amiri Naskh calligraphy.
     * Line-height dynamically scales with font size (1.85x) to prevent mark clipping.
     */
    fun arabicVerse(fontSize: Float = 26f): TextStyle = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 1.85f).sp,
        textAlign = TextAlign.End,
        letterSpacing = 0.sp
    )

    /**
     * Arabic Surah title badge (e.g. الفاتحة).
     */
    val arabicSurahTitle: TextStyle = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        textAlign = TextAlign.End
    )

    /**
     * Latin transliteration for learning recitation and tajwid phonetics.
     */
    val transliteration: TextStyle = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.2.sp
    )

    /**
     * Indonesian Ministry of Religious Affairs (Kemenag) verse translation.
     */
    val translation: TextStyle = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.5.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.15.sp
    )

    /**
     * Concise Indonesian Tafsir paragraph.
     */
    val tafsir: TextStyle = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    )
}

// ============================================================================
// 4. THEME MODES & COLOR SCHEMES (Strictly OLED Midnight Dark Only)
// ============================================================================

enum class AppThemeMode {
    DARK
}

// Minimalist, high-contrast Dark Scheme (OLED friendly)
private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color(0xFF04201A),
    primaryContainer = EmeraldGreenContainerDark,
    onPrimaryContainer = EmeraldLight,
    secondary = GoldAccent,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = GoldContainerDark,
    onSecondaryContainer = GoldLight,
    tertiary = CelestialCyan,
    onTertiary = Color(0xFF042F2C),
    tertiaryContainer = CelestialContainerDark,
    onTertiaryContainer = Color(0xFFA5F3FC),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainer = DarkCardSurface,
    surfaceContainerHigh = DarkSurfaceElevated,
    outline = DarkSurfaceBorder,
    outlineVariant = Color(0xFF1E2D44)
)

// ============================================================================
// 5. APPLICATION THEME COMPOSABLE
// ============================================================================

@Composable
fun QuranKitaTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.background.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = QuranKitaTypography,
        content = content
    )
}
