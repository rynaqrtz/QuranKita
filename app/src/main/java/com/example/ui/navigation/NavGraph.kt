package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.asmaulhusna.AsmaulHusnaScreen
import com.example.ui.dua.DuaScreen
import com.example.ui.dzikir.DzikirScreen
import com.example.ui.dzikir.DzikirViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.khatam.KhatamTrackerScreen
import com.example.ui.main.MainScreen
import com.example.ui.prayer.PrayerQiblaScreen
import com.example.ui.prayer.PrayerQiblaViewModel
import com.example.ui.quiz.QuranQuizScreen
import com.example.ui.quran.QuranListScreen
import com.example.ui.quran.QuranListViewModel
import com.example.ui.reader.SurahReaderScreen
import com.example.ui.reader.SurahReaderViewModel
import com.example.ui.search.SearchScreen
import com.example.ui.search.SearchViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.tahfidz.TahfidzScreen
import com.example.ui.tahfidz.TahfidzViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun QuranKitaApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom bar on reader, sub-features, dzikir, and search for maximum focus
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.QuranList.route,
        Screen.Tahfidz.route,
        Screen.PrayerQibla.route,
        Screen.Settings.route
    )

    MainScreen(
        currentRoute = currentRoute,
        showBottomBar = showBottomBar,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(Screen.Home.route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    ) { innerPadding ->
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding),
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> (fullWidth * 0.12f).toInt() },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -(fullWidth * 0.08f).toInt() },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeOut(
                            animationSpec = spring(stiffness = Spring.StiffnessMedium)
                        )
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -(fullWidth * 0.08f).toInt() },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> (fullWidth * 0.12f).toInt() },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeOut(
                            animationSpec = spring(stiffness = Spring.StiffnessMedium)
                        )
                    }
                ) {
                    composable(Screen.Home.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val homeViewModel: HomeViewModel = viewModel()
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToQuran = { navController.navigate(Screen.QuranList.route) },
                                onNavigateToSurah = { surahNo ->
                                    navController.navigate(Screen.SurahReader.createRoute(surahNo))
                                },
                                onNavigateToTahfidz = { navController.navigate(Screen.Tahfidz.route) },
                                onNavigateToPrayerQibla = { navController.navigate(Screen.PrayerQibla.route) },
                                onNavigateToDzikir = { navController.navigate(Screen.Dzikir.route) },
                                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                                onNavigateToDua = { navController.navigate(Screen.Dua.route) },
                                onNavigateToAsmaulHusna = { navController.navigate(Screen.AsmaulHusna.route) },
                                onNavigateToQuiz = { navController.navigate(Screen.QuranQuiz.route) },
                                onNavigateToKhatamTracker = { navController.navigate(Screen.KhatamTracker.route) }
                            )
                        }
                    }

                    composable(Screen.QuranList.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val quranViewModel: QuranListViewModel = viewModel()
                            QuranListScreen(
                                viewModel = quranViewModel,
                                onNavigateToSurah = { surahNo ->
                                    navController.navigate(Screen.SurahReader.createRoute(surahNo))
                                }
                            )
                        }
                    }

                    composable(
                        route = Screen.SurahReader.route,
                        arguments = listOf(
                            navArgument("surahNumber") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
                            val readerViewModel: SurahReaderViewModel = viewModel()
                            SurahReaderScreen(
                                surahNumber = surahNumber,
                                viewModel = readerViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }

                    composable(Screen.Tahfidz.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val tahfidzViewModel: TahfidzViewModel = viewModel()
                            TahfidzScreen(
                                viewModel = tahfidzViewModel,
                                onNavigateToSurah = { surahNo ->
                                    navController.navigate(Screen.SurahReader.createRoute(surahNo))
                                }
                            )
                        }
                    }

                    composable(Screen.PrayerQibla.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val prayerViewModel: PrayerQiblaViewModel = viewModel()
                            PrayerQiblaScreen(viewModel = prayerViewModel)
                        }
                    }

                    composable(Screen.Dzikir.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val dzikirViewModel: DzikirViewModel = viewModel()
                            DzikirScreen(
                                viewModel = dzikirViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }

                    composable(Screen.Dua.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            DuaScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }

                    composable(Screen.AsmaulHusna.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            AsmaulHusnaScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }

                    composable(Screen.QuranQuiz.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            QuranQuizScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }

                    composable(Screen.KhatamTracker.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            KhatamTrackerScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToSurah = { surahNo ->
                                    navController.navigate(Screen.SurahReader.createRoute(surahNo))
                                }
                            )
                        }
                    }

                    composable(Screen.Search.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val searchViewModel: SearchViewModel = viewModel()
                            SearchScreen(
                                viewModel = searchViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToSurah = { surahNo ->
                                    navController.navigate(Screen.SurahReader.createRoute(surahNo))
                                }
                            )
                        }
                    }

                    composable(Screen.Settings.route) {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val settingsViewModel: SettingsViewModel = viewModel()
                            SettingsScreen(viewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }
}
