import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../data/quran_repository.dart';
import '../models/quran_verse.dart';
import '../services/favorites_service.dart';
import '../theme/app_theme.dart';

class AyahReaderScreen extends StatefulWidget {
  final List<QuranVerse>? verses;
  final int? currentIndex;
  final VoidCallback? onNext;
  final VoidCallback? onPrev;
  final bool? isFavorite;
  final Function(QuranVerse)? onToggleFavorite;
  final String? initialMoodId;
  final String? initialVerseId;

  const AyahReaderScreen({
    super.key,
    this.verses,
    this.currentIndex,
    this.onNext,
    this.onPrev,
    this.isFavorite,
    this.onToggleFavorite,
    this.initialMoodId,
    this.initialVerseId,
  });

  @override
  State<AyahReaderScreen> createState() => _AyahReaderScreenState();
}

class _AyahReaderScreenState extends State<AyahReaderScreen> {
  late List<QuranVerse> _activeVerses;
  late int _activeIndex;
  final Set<String> _localFavorites = {};

  @override
  void initState() {
    super.initState();
    _initVerses();
    _loadFavorites();
  }

  void _initVerses() {
    if (widget.verses != null && widget.verses!.isNotEmpty) {
      _activeVerses = widget.verses!;
      _activeIndex = (widget.currentIndex ?? 0).clamp(0, _activeVerses.length - 1);
    } else {
      final mood = widget.initialMoodId ?? 'all';
      _activeVerses = QuranRepository.getVersesByMood(mood);
      if (_activeVerses.isEmpty) {
        _activeVerses = QuranRepository.getAllVerses();
      }
      if (widget.initialVerseId != null) {
        final found = _activeVerses.indexWhere((v) => v.id == widget.initialVerseId);
        _activeIndex = found >= 0 ? found : 0;
      } else {
        _activeIndex = 0;
      }
    }
  }

  Future<void> _loadFavorites() async {
    final favs = await FavoritesService.getLocalFavorites();
    if (mounted) {
      setState(() {
        _localFavorites.clear();
        _localFavorites.addAll(favs);
      });
    }
  }

  void _handleNext() {
    if (widget.onNext != null) {
      widget.onNext!();
    } else {
      if (_activeIndex < _activeVerses.length - 1) {
        setState(() => _activeIndex++);
      } else {
        setState(() => _activeIndex = 0);
      }
    }
  }

  void _handlePrev() {
    if (widget.onPrev != null) {
      widget.onPrev!();
    } else {
      if (_activeIndex > 0) {
        setState(() => _activeIndex--);
      } else {
        setState(() => _activeIndex = _activeVerses.length - 1);
      }
    }
  }

  Future<void> _handleToggleFavorite(QuranVerse verse) async {
    if (widget.onToggleFavorite != null) {
      widget.onToggleFavorite!(verse);
    } else {
      final isNowFav = await FavoritesService.toggleFavorite(verse.id);
      setState(() {
        if (isNowFav) {
          _localFavorites.add(verse.id);
        } else {
          _localFavorites.remove(verse.id);
        }
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(isNowFav ? 'Saved to Favorites ❤️' : 'Removed from Favorites'),
            duration: const Duration(seconds: 1),
          ),
        );
      }
    }
  }

  void _copyVerse(QuranVerse verse) {
    Clipboard.setData(ClipboardData(
      text: '${verse.arabicText}\n\n"${verse.translation}"\n— ${verse.reference}',
    ));
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Ayah copied to clipboard'),
        duration: Duration(seconds: 2),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_activeVerses.isEmpty) {
      return const Scaffold(
        backgroundColor: AppColors.emeraldDeep,
        body: Center(
          child: Text(
            "No verses available for this category.",
            style: TextStyle(color: AppColors.textSecondary),
          ),
        ),
      );
    }

    final verse = _activeVerses[_activeIndex.clamp(0, _activeVerses.length - 1)];
    final bool isFav = widget.isFavorite ?? _localFavorites.contains(verse.id);

    return Scaffold(
      backgroundColor: AppColors.emeraldDeep,
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              verse.surahName,
              style: const TextStyle(
                color: AppColors.textPrimary,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            Text(
              verse.arabicMood,
              style: const TextStyle(color: AppColors.goldLight, fontSize: 11),
            ),
          ],
        ),
        actions: [
          IconButton(
            tooltip: 'Copy Ayah',
            icon: const Icon(Icons.copy, size: 20, color: AppColors.goldAccent),
            onPressed: () => _copyVerse(verse),
          ),
          IconButton(
            onPressed: () => _handleToggleFavorite(verse),
            icon: Icon(
              isFav ? Icons.favorite : Icons.favorite_border,
              color: isFav ? Colors.redAccent : AppColors.goldAccent,
            ),
            tooltip: "Save to Favorites",
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 12.0),
        child: Column(
          children: [
            // Top Reference Pill
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
              decoration: BoxDecoration(
                color: AppColors.emeraldSurfaceVariant,
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: AppColors.borderSubtle),
              ),
              child: Text(
                "${verse.surahName} • Ayah ${verse.ayahNumber}",
                style: const TextStyle(
                  color: AppColors.goldAccent,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            const SizedBox(height: 24),

            // Arabic Calligraphy Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: AppColors.emeraldSurface,
                borderRadius: BorderRadius.circular(24),
                border: Border.all(color: AppColors.borderSubtle),
              ),
              child: Column(
                children: [
                  Text(
                    verse.arabicText,
                    style: const TextStyle(
                      color: AppColors.goldLight,
                      fontSize: 26,
                      fontWeight: FontWeight.bold,
                      height: 1.8,
                    ),
                    textAlign: TextAlign.center,
                    textDirection: TextDirection.rtl,
                  ),
                  const SizedBox(height: 20),
                  const Divider(color: AppColors.borderSubtle, thickness: 0.5),
                  const SizedBox(height: 12),

                  // Transliteration
                  Text(
                    verse.transliteration,
                    style: const TextStyle(
                      color: AppColors.textSecondary,
                      fontSize: 13,
                      fontStyle: FontStyle.italic,
                      height: 1.4,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // English Translation Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                color: AppColors.emeraldSurface,
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: AppColors.borderSubtle),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    "Translation:",
                    style: TextStyle(
                      color: AppColors.goldLight,
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    "\"${verse.translation}\"",
                    style: const TextStyle(
                      color: AppColors.textPrimary,
                      fontSize: 14.5,
                      height: 1.45,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 14),

            // Spiritual Reflection Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                color: AppColors.emeraldSurfaceVariant.withValues(alpha: 0.6),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: AppColors.borderSubtle),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Row(
                    children: [
                      Icon(Icons.lightbulb_outline, color: AppColors.goldAccent, size: 16),
                      SizedBox(width: 6),
                      Text(
                        "Spiritual Reflection:",
                        style: TextStyle(
                          color: AppColors.goldLight,
                          fontSize: 11,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Text(
                    verse.reflection,
                    style: const TextStyle(
                      color: AppColors.textSecondary,
                      fontSize: 13,
                      height: 1.4,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // Prev / Next Buttons
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                OutlinedButton.icon(
                  onPressed: _handlePrev,
                  style: OutlinedButton.styleFrom(
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    side: const BorderSide(color: AppColors.borderSubtle),
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  icon: const Icon(Icons.arrow_back_ios, size: 14, color: AppColors.goldLight),
                  label: const Text("Previous", style: TextStyle(color: AppColors.goldLight)),
                ),
                Text(
                  "${_activeIndex + 1} of ${_activeVerses.length}",
                  style: const TextStyle(color: AppColors.textMuted, fontSize: 12),
                ),
                ElevatedButton.icon(
                  onPressed: _handleNext,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.emeraldPrimary,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  ),
                  label: const Text(
                    "Next Ayah",
                    style: TextStyle(color: AppColors.emeraldDeep, fontWeight: FontWeight.bold),
                  ),
                  icon: const Icon(Icons.arrow_forward_ios, size: 14, color: AppColors.emeraldDeep),
                ),
              ],
            ),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }
}
