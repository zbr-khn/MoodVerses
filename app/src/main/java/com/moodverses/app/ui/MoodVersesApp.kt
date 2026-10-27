package com.moodverses.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.moodverses.app.auth.AuthDialog
import com.moodverses.app.auth.AuthManager
import com.moodverses.app.data.FavoritesManager
import com.moodverses.app.data.QuranRepository
import com.moodverses.app.model.QuranVerse
import com.moodverses.app.ui.components.OnboardingManager
import com.moodverses.app.ui.components.OnboardingTourDialog
import com.moodverses.app.ui.components.WaleeyLoginPromptDialog
import com.moodverses.app.ui.screens.AyahReaderScreen
import com.moodverses.app.ui.screens.FavoritesScreen
import com.moodverses.app.ui.screens.HomeScreen
import com.moodverses.app.ui.screens.TasbihScreen
import com.moodverses.app.ui.screens.WaleeyScreen
import com.moodverses.app.ui.theme.EmeraldDeep
import com.moodverses.app.ui.theme.EmeraldSurface
import com.moodverses.app.ui.theme.GoldAccent

enum class AppTab(val label: String) {
    HOME("Explore"),
    AYAT("Ayat"),
    WALEEY("Waleey"),
    TASBIH("Tasbih"),
    SAVED("Saved")
}

@Composable
fun MoodVersesApp() {
    val context = LocalContext.current

    // Initialize Favorites Persistence
    LaunchedEffect(Unit) {
        FavoritesManager.init(context)
    }

    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var selectedMoodId by remember { mutableStateOf("all") }
    var currentAyahIndex by remember { mutableIntStateOf(0) }

    // Dialog States
    var showAuthDialog by remember { mutableStateOf(false) }
    var showOnboardingTour by remember { mutableStateOf(!OnboardingManager.hasCompletedTour(context)) }
    var showWaleeyLoginPrompt by remember { mutableStateOf(false) }
    var hasDismissedWaleeyPrompt by remember { mutableStateOf(false) }

    // User Login State
    var isLoggedIn by remember { mutableStateOf(AuthManager.isLoggedIn) }
    val userDisplay = remember(isLoggedIn, AuthManager.currentUser) {
        AuthManager.currentUser?.displayName 
            ?: AuthManager.currentUser?.phoneNumber 
            ?: AuthManager.currentUser?.email
    }

    // Favorites State backed by SharedPreferences + Cloud
    val favorites = remember { mutableStateListOf<QuranVerse>() }

    // Load initial favorites from local storage & cloud
    LaunchedEffect(Unit) {
        val localIds = FavoritesManager.getLocalFavoriteIds()
        favorites.clear()
        favorites.addAll(QuranRepository.verses.filter { it.id in localIds })

        // If user is already logged in, fetch and merge cloud favorites
        val user = AuthManager.currentUser
        if (user != null) {
            FavoritesManager.fetchAndMergeCloudFavorites(user.uid) { mergedIds ->
                favorites.clear()
                favorites.addAll(QuranRepository.verses.filter { it.id in mergedIds })
            }
        }
    }

    val filteredVerses = remember(selectedMoodId) {
        if (selectedMoodId == "all") {
            QuranRepository.verses
        } else {
            QuranRepository.verses.filter { it.mood == selectedMoodId }
        }
    }

    Scaffold(
        containerColor = EmeraldDeep,
        bottomBar = {
            NavigationBar(
                containerColor = EmeraldSurface
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.HOME,
                    onClick = { currentTab = AppTab.HOME },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDeep,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.AYAT,
                    onClick = { currentTab = AppTab.AYAT },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Ayat") },
                    label = { Text("Ayat") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDeep,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.WALEEY,
                    onClick = {
                        if (!isLoggedIn && !hasDismissedWaleeyPrompt) {
                            showWaleeyLoginPrompt = true
                        } else {
                            currentTab = AppTab.WALEEY
                        }
                    },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Waleey") },
                    label = { Text("Waleey") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDeep,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.TASBIH,
                    onClick = { currentTab = AppTab.TASBIH },
                    icon = { Icon(Icons.Default.TouchApp, contentDescription = "Tasbih") },
                    label = { Text("Tasbih") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDeep,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.SAVED,
                    onClick = { currentTab = AppTab.SAVED },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Saved") },
                    label = { Text("Saved") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldDeep,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues),
            color = EmeraldDeep
        ) {
            when (currentTab) {
                AppTab.HOME -> {
                    HomeScreen(
                        onSelectMood = { moodId ->
                            selectedMoodId = moodId
                            currentAyahIndex = 0
                            currentTab = AppTab.AYAT
                        },
                        onRandomVerse = {
                            selectedMoodId = "all"
                            currentAyahIndex = (0 until QuranRepository.verses.size).random()
                            currentTab = AppTab.AYAT
                        }
                    )
                }
                AppTab.AYAT -> {
                    val currentVerse = filteredVerses.getOrNull(currentAyahIndex.coerceIn(0, filteredVerses.lastIndex))
                    AyahReaderScreen(
                        verses = filteredVerses,
                        currentIndex = currentAyahIndex,
                        onNext = {
                            if (filteredVerses.isNotEmpty()) {
                                currentAyahIndex = (currentAyahIndex + 1) % filteredVerses.size
                            }
                        },
                        onPrev = {
                            if (filteredVerses.isNotEmpty()) {
                                currentAyahIndex = (currentAyahIndex - 1 + filteredVerses.size) % filteredVerses.size
                            }
                        },
                        isFavorite = currentVerse?.let { favorites.contains(it) } ?: false,
                        onToggleFavorite = { verse ->
                            val isNowFav = FavoritesManager.toggleFavorite(verse.id)
                            if (isNowFav) {
                                if (!favorites.contains(verse)) favorites.add(verse)
                            } else {
                                favorites.remove(verse)
                            }
                        }
                    )
                }
                AppTab.WALEEY -> {
                    WaleeyScreen()
                }
                AppTab.TASBIH -> {
                    TasbihScreen()
                }
                AppTab.SAVED -> {
                    FavoritesScreen(
                        favorites = favorites,
                        isLoggedIn = isLoggedIn,
                        userDisplay = userDisplay,
                        onSignInClick = { showAuthDialog = true },
                        onSelectAyah = { verse ->
                            val index = QuranRepository.verses.indexOf(verse)
                            selectedMoodId = "all"
                            if (index >= 0) {
                                currentAyahIndex = index
                            }
                            currentTab = AppTab.AYAT
                        },
                        onRemoveFavorite = { verse ->
                            FavoritesManager.toggleFavorite(verse.id)
                            favorites.remove(verse)
                        }
                    )
                }
            }
        }
    }

    // 1. First-Install Guided Tour Dialog
    if (showOnboardingTour) {
        OnboardingTourDialog(
            onDismiss = { showOnboardingTour = false }
        )
    }

    // 2. Waleey AI Login Prompt (Shown when unauthenticated user navigates to Waleey)
    if (showWaleeyLoginPrompt) {
        WaleeyLoginPromptDialog(
            onDismiss = {
                hasDismissedWaleeyPrompt = true
                showWaleeyLoginPrompt = false
                currentTab = AppTab.WALEEY
            },
            onSignInClick = {
                showWaleeyLoginPrompt = false
                showAuthDialog = true
            }
        )
    }

    // 3. Main Auth Dialog (Mobile OTP, Google, Email/Password)
    if (showAuthDialog) {
        AuthDialog(
            onDismiss = { showAuthDialog = false },
            onAuthSuccess = {
                isLoggedIn = true
                showAuthDialog = false
                hasDismissedWaleeyPrompt = true

                // Sync favorites with cloud
                val user = AuthManager.currentUser
                if (user != null) {
                    FavoritesManager.fetchAndMergeCloudFavorites(user.uid) { mergedIds ->
                        favorites.clear()
                        favorites.addAll(QuranRepository.verses.filter { it.id in mergedIds })
                    }
                }
            }
        )
    }
}
