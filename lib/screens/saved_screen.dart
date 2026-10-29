import 'package:flutter/material.dart';
import '../data/quran_repository.dart';
import '../models/quran_verse.dart';
import '../services/auth_service.dart';
import '../services/favorites_service.dart';
import '../theme/app_theme.dart';
import '../components/user_profile_dialog.dart';
import 'ayah_reader_screen.dart';

class SavedScreen extends StatefulWidget {
  final Function(String moodId, int ayahIndex)? onOpenAyah;

  const SavedScreen({super.key, this.onOpenAyah});

  @override
  State<SavedScreen> createState() => _SavedScreenState();
}

class _SavedScreenState extends State<SavedScreen> {
  Set<String> _favoriteIds = {};
  bool _isLoading = true;
  bool _isSyncing = false;

  @override
  void initState() {
    super.initState();
    _loadFavorites();
  }

  Future<void> _loadFavorites() async {
    setState(() => _isLoading = true);
    final user = AuthService.currentUser;
    Set<String> ids;
    if (user != null) {
      ids = await FavoritesService.fetchAndMergeCloudFavorites(user.uid);
    } else {
      ids = await FavoritesService.getLocalFavorites();
    }
    if (mounted) {
      setState(() {
        _favoriteIds = ids;
        _isLoading = false;
      });
    }
  }

  Future<void> _removeFavorite(String verseId) async {
    await FavoritesService.toggleFavorite(verseId);
    setState(() {
      _favoriteIds.remove(verseId);
    });
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Ayah removed from saved collection'),
          duration: Duration(seconds: 2),
        ),
      );
    }
  }

  void _confirmLogout() {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: AppTheme.cardBackground,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        title: const Text("Log Out?", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: const Text(
          "Are you sure you want to log out from MoodVerses?",
          style: TextStyle(color: AppTheme.textSecondary, fontSize: 13.5),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text("Cancel", style: TextStyle(color: AppTheme.textSecondary)),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.redAccent.shade700,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
            ),
            onPressed: () async {
              Navigator.pop(ctx);
              await AuthService.signOut();
              await _loadFavorites();
              if (mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text("You are logged out.", style: TextStyle(fontWeight: FontWeight.bold)),
                    backgroundColor: Color(0xFF11382B),
                    behavior: SnackBarBehavior.floating,
                    duration: Duration(seconds: 3),
                  ),
                );
              }
            },
            child: const Text("Yes, Log Out", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final user = AuthService.currentUser;
    final savedVerses = QuranRepository.getAllVerses()
        .where((v) => _favoriteIds.contains(v.id))
        .toList();

    return Scaffold(
      backgroundColor: AppTheme.darkBackground,
      appBar: AppBar(
        title: const Text(
          'Saved Verses',
          style: TextStyle(fontWeight: FontWeight.bold, color: AppTheme.goldColor),
        ),
        actions: [
          IconButton(
            icon: _isSyncing
                ? const SizedBox(
                    width: 18,
                    height: 18,
                    child: CircularProgressIndicator(strokeWidth: 2, color: AppTheme.goldColor),
                  )
                : const Icon(Icons.sync, color: AppTheme.goldColor),
            tooltip: 'Sync Cloud Favorites',
            onPressed: () async {
              setState(() => _isSyncing = true);
              await _loadFavorites();
              setState(() => _isSyncing = false);
            },
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator(color: AppTheme.goldColor))
          : RefreshIndicator(
              onRefresh: _loadFavorites,
              color: AppTheme.goldColor,
              backgroundColor: AppTheme.cardBackground,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  // Cloud Sync Status Banner
                  _buildCloudSyncBanner(user),
                  const SizedBox(height: 16),

                  if (savedVerses.isEmpty)
                    _buildEmptyState()
                  else ...[
                    Padding(
                      padding: const EdgeInsets.only(bottom: 12),
                      child: Text(
                        '${savedVerses.length} Saved Reflections',
                        style: const TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                          color: AppTheme.textSecondary,
                        ),
                      ),
                    ),
                    for (final verse in savedVerses)
                      _buildSavedVerseCard(verse),
                  ],
                ],
              ),
            ),
    );
  }

  Widget _buildCloudSyncBanner(dynamic user) {
    final bool isLoggedIn = user != null;

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: isLoggedIn
            ? AppTheme.emeraldPrimary.withValues(alpha: 0.15)
            : const Color(0xFF2D2310),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: isLoggedIn
              ? AppTheme.emeraldLight.withValues(alpha: 0.4)
              : AppTheme.goldColor.withValues(alpha: 0.4),
          width: 1,
        ),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: isLoggedIn
                  ? AppTheme.emeraldPrimary.withValues(alpha: 0.25)
                  : AppTheme.goldColor.withValues(alpha: 0.2),
              shape: BoxShape.circle,
            ),
            child: Icon(
              isLoggedIn ? Icons.cloud_done : Icons.cloud_off,
              color: isLoggedIn ? AppTheme.emeraldLight : AppTheme.goldColor,
              size: 20,
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  isLoggedIn ? 'Cloud Sync Active' : 'Offline / Guest Mode',
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.bold,
                    color: isLoggedIn ? AppTheme.emeraldLight : AppTheme.goldColor,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  isLoggedIn
                      ? 'Your saved Ayahs and reflections are securely backed up to Firebase and synced across your devices.'
                      : 'Saved verses are only stored locally on this phone. Sign in to back them up to your cloud account.',
                  style: const TextStyle(fontSize: 12, color: AppTheme.textSecondary, height: 1.4),
                ),
                if (!isLoggedIn) ...[
                  const SizedBox(height: 10),
                  ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppTheme.goldColor,
                      foregroundColor: Colors.black,
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      textStyle: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                    ),
                    onPressed: () {
                      UserProfileDialog.show(context, onProfileUpdated: _loadFavorites);
                    },
                    child: const Text('Open Profile & Sign In'),
                  ),
                ] else ...[
                  const SizedBox(height: 10),
                  Row(
                    children: [
                      ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: AppTheme.emeraldPrimary,
                          foregroundColor: AppColors.emeraldDeep,
                          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                          textStyle: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                        ),
                        onPressed: () {
                          UserProfileDialog.show(context, onProfileUpdated: _loadFavorites);
                        },
                        child: const Text('View Profile'),
                      ),
                      const SizedBox(width: 8),
                      OutlinedButton.icon(
                        style: OutlinedButton.styleFrom(
                          foregroundColor: Colors.redAccent,
                          side: BorderSide(color: Colors.redAccent.withValues(alpha: 0.5)),
                          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                          textStyle: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                        ),
                        onPressed: _confirmLogout,
                        icon: const Icon(Icons.logout, size: 14),
                        label: const Text('Log Out'),
                      ),
                    ],
                  ),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSavedVerseCard(QuranVerse verse) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      decoration: BoxDecoration(
        color: AppTheme.cardBackground,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: Colors.white10),
      ),
      child: InkWell(
        borderRadius: BorderRadius.circular(16),
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (ctx) => AyahReaderScreen(
                initialMoodId: verse.mood,
                initialVerseId: verse.id,
              ),
            ),
          );
        },
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                    decoration: BoxDecoration(
                      color: AppTheme.emeraldPrimary.withValues(alpha: 0.2),
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: AppTheme.emeraldLight.withValues(alpha: 0.3)),
                    ),
                    child: Text(
                      verse.moodLabel,
                      style: const TextStyle(
                        fontSize: 11,
                        fontWeight: FontWeight.w600,
                        color: AppTheme.emeraldLight,
                      ),
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.favorite, color: Colors.redAccent, size: 20),
                    tooltip: 'Remove from saved',
                    onPressed: () => _removeFavorite(verse.id),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                verse.reference,
                style: const TextStyle(
                  fontSize: 15,
                  fontWeight: FontWeight.bold,
                  color: AppTheme.goldColor,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                verse.arabicText,
                textAlign: TextAlign.right,
                textDirection: TextDirection.rtl,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: AppTheme.textPrimary,
                  fontFamily: 'Amiri',
                ),
              ),
              const SizedBox(height: 8),
              Text(
                verse.translation,
                maxLines: 3,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(
                  fontSize: 13,
                  color: AppTheme.textSecondary,
                  height: 1.4,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildEmptyState() {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 60, horizontal: 24),
      alignment: Alignment.center,
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: AppTheme.cardBackground,
              border: Border.all(color: AppTheme.goldColor.withValues(alpha: 0.2), width: 1.5),
            ),
            child: const Icon(
              Icons.bookmark_border,
              size: 48,
              color: AppTheme.goldColor,
            ),
          ),
          const SizedBox(height: 20),
          const Text(
            'No Saved Verses Yet',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: AppTheme.textPrimary,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'When you are reading an Ayah that touches your heart, tap the heart icon to save it here for daily contemplation.',
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 13,
              color: AppTheme.textSecondary,
              height: 1.5,
            ),
          ),
        ],
      ),
    );
  }
}
