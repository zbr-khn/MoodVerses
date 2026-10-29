import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:firebase_core/firebase_core.dart';
import 'dart:math';

import 'firebase_options.dart';
import 'theme/app_theme.dart';
import 'data/quran_repository.dart';
import 'services/auth_service.dart';
import 'components/auth_dialog.dart';
import 'components/onboarding_tour_dialog.dart';
import 'components/waleey_login_prompt_dialog.dart';
import 'screens/explore_screen.dart';
import 'screens/ayah_reader_screen.dart';
import 'screens/waleey_screen.dart';
import 'screens/tasbih_screen.dart';
import 'screens/saved_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Custom Error Widget so that any build exception renders gracefully with diagnostics instead of a blank gray screen
  ErrorWidget.builder = (FlutterErrorDetails details) {
    return Scaffold(
      backgroundColor: AppColors.emeraldDeep,
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Icon(Icons.info_outline, color: AppColors.goldAccent, size: 48),
                const SizedBox(height: 16),
                const Text(
                  "MoodVerses Encountered an Issue",
                  style: TextStyle(color: AppColors.goldLight, fontSize: 18, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 8),
                SelectableText(
                  details.exceptionAsString(),
                  style: const TextStyle(color: AppColors.textSecondary, fontSize: 12),
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  };

  // Set system UI overlay style
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.light,
      systemNavigationBarColor: AppColors.emeraldDeep,
      systemNavigationBarIconBrightness: Brightness.light,
    ),
  );

  try {
    await Firebase.initializeApp(
      options: DefaultFirebaseOptions.currentPlatform,
    );
  } catch (e) {
    debugPrint("Firebase initialization info: $e");
  }

  runApp(const MoodVersesApp());
}

class MoodVersesApp extends StatelessWidget {
  const MoodVersesApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'MoodVerses',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.darkTheme,
      home: const MainNavigationScreen(),
    );
  }
}

class MainNavigationScreen extends StatefulWidget {
  const MainNavigationScreen({super.key});

  @override
  State<MainNavigationScreen> createState() => _MainNavigationScreenState();
}

class _MainNavigationScreenState extends State<MainNavigationScreen> {
  int _currentIndex = 0;
  String _selectedMoodId = 'all';
  int _currentAyahIndex = 0;
  bool _hasShownWaleeyLoginPrompt = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _checkFirstTimeTour();
    });
  }

  Future<void> _checkFirstTimeTour() async {
    try {
      final completed = await OnboardingManager.hasCompletedTour();
      if (!completed && mounted) {
        showDialog(
          context: context,
          barrierDismissible: false,
          builder: (ctx) => OnboardingTourDialog(
            onDismiss: () => Navigator.of(ctx).pop(),
          ),
        );
      }
    } catch (e) {
      debugPrint("Tour check info: $e");
    }
  }

  void _onTabTapped(int index) {
    // If navigating to Waleey tab (index 2)
    if (index == 2 && AuthService.currentUser == null && !_hasShownWaleeyLoginPrompt) {
      _hasShownWaleeyLoginPrompt = true;
      setState(() => _currentIndex = index);
      showDialog(
        context: context,
        builder: (ctx) => WaleeyLoginPromptDialog(
          onDismiss: () => Navigator.of(ctx).pop(),
          onSignInClick: () {
            Navigator.of(ctx).pop();
            _openAuthDialog();
          },
        ),
      );
      return;
    }

    setState(() => _currentIndex = index);
  }

  void _openAuthDialog() {
    showDialog(
      context: context,
      builder: (ctx) => AuthDialog(
        onDismiss: () => Navigator.of(ctx).pop(),
        onSuccess: () {
          setState(() {}); // refresh UI with logged in state
        },
      ),
    );
  }

  void _selectMoodAndOpenAyah(String moodId) {
    setState(() {
      _selectedMoodId = moodId;
      _currentAyahIndex = 0;
      _currentIndex = 1; // Ayat tab
    });
  }

  void _openRandomAyah() {
    final allVerses = QuranRepository.getAllVerses();
    if (allVerses.isNotEmpty) {
      final random = Random();
      final index = random.nextInt(allVerses.length);
      setState(() {
        _selectedMoodId = 'all';
        _currentAyahIndex = index;
        _currentIndex = 1; // Ayat tab
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    // Build the active screen dynamically to preserve parameter updates
    Widget activeScreen;
    switch (_currentIndex) {
      case 0:
        activeScreen = ExploreScreen(
          onSelectMood: _selectMoodAndOpenAyah,
          onRandomVerse: _openRandomAyah,
        );
        break;
      case 1:
        activeScreen = AyahReaderScreen(
          key: ValueKey('ayah_${_selectedMoodId}_$_currentAyahIndex'),
          initialMoodId: _selectedMoodId,
          currentIndex: _currentAyahIndex,
        );
        break;
      case 2:
        activeScreen = const WaleeyScreen();
        break;
      case 3:
        activeScreen = const TasbihScreen();
        break;
      case 4:
        activeScreen = SavedScreen(
          onOpenAyah: (moodId, ayahIndex) {
            setState(() {
              _selectedMoodId = moodId;
              _currentAyahIndex = ayahIndex;
              _currentIndex = 1;
            });
          },
        );
        break;
      default:
        activeScreen = ExploreScreen(
          onSelectMood: _selectMoodAndOpenAyah,
          onRandomVerse: _openRandomAyah,
        );
    }

    return Scaffold(
      backgroundColor: AppColors.emeraldDeep,
      body: activeScreen,
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: _onTabTapped,
        backgroundColor: AppColors.emeraldSurface,
        indicatorColor: AppColors.goldAccent.withValues(alpha: 0.2),
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.explore_outlined, color: AppColors.textMuted),
            selectedIcon: Icon(Icons.explore, color: AppColors.goldAccent),
            label: 'Explore',
          ),
          NavigationDestination(
            icon: Icon(Icons.menu_book_outlined, color: AppColors.textMuted),
            selectedIcon: Icon(Icons.menu_book, color: AppColors.goldAccent),
            label: 'Ayat',
          ),
          NavigationDestination(
            icon: Icon(Icons.auto_awesome_outlined, color: AppColors.textMuted),
            selectedIcon: Icon(Icons.auto_awesome, color: AppColors.goldAccent),
            label: 'Waleey AI',
          ),
          NavigationDestination(
            icon: Icon(Icons.touch_app_outlined, color: AppColors.textMuted),
            selectedIcon: Icon(Icons.touch_app, color: AppColors.goldAccent),
            label: 'Tasbih',
          ),
          NavigationDestination(
            icon: Icon(Icons.favorite_border, color: AppColors.textMuted),
            selectedIcon: Icon(Icons.favorite, color: AppColors.goldAccent),
            label: 'Saved',
          ),
        ],
      ),
    );
  }
}
