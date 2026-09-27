package com.example.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CompassCalibration
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.EmeraldGreen

/**
 * Navigation item specification for the modern minimal BottomNavigationBar.
 */
data class MainNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val MAIN_NAV_ITEMS = listOf(
    MainNavItem(
        route = Screen.Home.route,
        title = "Beranda",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home"
    ),
    MainNavItem(
        route = Screen.QuranList.route,
        title = "Al-Qur'an",
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
        testTag = "nav_quran"
    ),
    MainNavItem(
        route = Screen.Tahfidz.route,
        title = "Tahfidz",
        selectedIcon = Icons.Filled.Psychology,
        unselectedIcon = Icons.Outlined.Psychology,
        testTag = "nav_tahfidz"
    ),
    MainNavItem(
        route = Screen.PrayerQibla.route,
        title = "Kiblat",
        selectedIcon = Icons.Filled.CompassCalibration,
        unselectedIcon = Icons.Outlined.CompassCalibration,
        testTag = "nav_prayer_qibla"
    ),
    MainNavItem(
        route = Screen.Settings.route,
        title = "Pengaturan",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        testTag = "nav_settings"
    )
)

/**
 * Base Scaffold structure in MainScreen.kt that implements a modern BottomNavigationBar
 * using Emerald Green (#10B981) as the active indicator, ensuring a clean, minimal design aesthetic.
 */
@Composable
fun MainScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    showBottomBar: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                ModernBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = onNavigate
                )
            }
        },
        content = content
    )
}

/**
 * Modern, clean, minimal BottomNavigationBar with Emerald Green (#10B981) active indicator.
 */
@Composable
fun ModernBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Ultra-clean 1.dp hairline top divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        )

        NavigationBar(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("modern_bottom_nav_bar"),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            MAIN_NAV_ITEMS.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                letterSpacing = 0.2.sp
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        // Active indicator uses Emerald Green (#10B981)
                        indicatorColor = EmeraldGreen,
                        selectedIconColor = Color(0xFF080D16),
                        selectedTextColor = EmeraldGreen,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
