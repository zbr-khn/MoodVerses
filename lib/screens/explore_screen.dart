import 'package:flutter/material.dart';
import '../data/quran_repository.dart';
import '../theme/app_theme.dart';

class ExploreScreen extends StatelessWidget {
  final Function(String moodId) onSelectMood;
  final VoidCallback onRandomVerse;

  const ExploreScreen({
    super.key,
    required this.onSelectMood,
    required this.onRandomVerse,
  });

  @override
  Widget build(BuildContext context) {
    final moods = QuranRepository.moods.where((m) => m.id != "all").toList();

    return Scaffold(
      backgroundColor: AppColors.emeraldDeep,
      appBar: AppBar(
        title: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(6),
              decoration: BoxDecoration(
                color: AppColors.emeraldSurfaceVariant,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: AppColors.borderSubtle),
              ),
              child: const Icon(Icons.explore, color: AppColors.goldAccent, size: 20),
            ),
            const SizedBox(width: 10),
            const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text("MoodVerses", style: TextStyle(color: AppColors.textPrimary, fontSize: 18, fontWeight: FontWeight.bold)),
                Text("آيات السكينة وطمأنينة القلوب", style: TextStyle(color: AppColors.goldLight, fontSize: 11)),
              ],
            ),
          ],
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Hero Contemplation Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [AppColors.emeraldSurfaceVariant, AppColors.emeraldSurface],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: AppColors.goldAccent.withValues(alpha: 0.4)),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: AppColors.goldAccent.withValues(alpha: 0.2),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: const Text("DAILY CONTEMPLATION", style: TextStyle(color: AppColors.goldLight, fontSize: 10, fontWeight: FontWeight.bold)),
                      ),
                      IconButton(
                        onPressed: onRandomVerse,
                        icon: const Icon(Icons.shuffle, color: AppColors.goldAccent, size: 20),
                        tooltip: "Random Ayah",
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                    style: TextStyle(
                      color: AppColors.goldLight,
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      height: 1.6,
                    ),
                  ),
                  const SizedBox(height: 6),
                  const Text(
                    "\"Unquestionably, by the remembrance of Allah do hearts find rest.\" - Surah Ar-Ra'd (13:28)",
                    style: TextStyle(color: AppColors.textSecondary, fontSize: 13, fontStyle: FontStyle.italic),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            const Text(
              "HOW IS YOUR HEART FEELING?",
              style: TextStyle(
                color: AppColors.goldLight,
                fontSize: 12,
                fontWeight: FontWeight.bold,
                letterSpacing: 1.2,
              ),
            ),
            const SizedBox(height: 4),
            const Text(
              "Select your state to discover verses sent for your soul:",
              style: TextStyle(color: AppColors.textMuted, fontSize: 12),
            ),
            const SizedBox(height: 14),

            // 12 Mood Grid
            GridView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                crossAxisSpacing: 12,
                mainAxisSpacing: 12,
                childAspectRatio: 1.25,
              ),
              itemCount: moods.length,
              itemBuilder: (context, index) {
                final mood = moods[index];
                return InkWell(
                  onTap: () => onSelectMood(mood.id),
                  borderRadius: BorderRadius.circular(16),
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                    decoration: BoxDecoration(
                      color: AppColors.emeraldSurface,
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(color: AppColors.borderSubtle),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Container(
                              padding: const EdgeInsets.all(6),
                              decoration: BoxDecoration(
                                color: AppColors.emeraldSurfaceVariant,
                                borderRadius: BorderRadius.circular(10),
                                border: Border.all(color: AppColors.borderSubtle),
                              ),
                              child: Icon(_getMoodIcon(mood.id), color: AppColors.goldAccent, size: 20),
                            ),
                            Flexible(
                              child: Text(
                                mood.arabicName,
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                                style: const TextStyle(
                                  color: AppColors.goldAccent,
                                  fontSize: 12,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ],
                        ),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              mood.name,
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                              style: const TextStyle(
                                color: AppColors.textPrimary,
                                fontSize: 13.5,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            const SizedBox(height: 2),
                            Text(
                              mood.description,
                              style: const TextStyle(color: AppColors.textMuted, fontSize: 10),
                              maxLines: 1,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  IconData _getMoodIcon(String moodId) {
    switch (moodId) {
      case 'all':
        return Icons.auto_stories;
      case 'anxious':
        return Icons.spa;
      case 'sad':
        return Icons.water_drop;
      case 'grateful':
        return Icons.wb_sunny;
      case 'lonely':
        return Icons.nights_stay;
      case 'angry':
        return Icons.self_improvement;
      case 'lost':
        return Icons.explore;
      case 'heartbroken':
        return Icons.healing;
      case 'fearful':
        return Icons.shield;
      case 'guilty':
        return Icons.volunteer_activism;
      case 'exhausted':
        return Icons.bedtime;
      case 'doubtful':
        return Icons.lightbulb;
      case 'dua':
        return Icons.menu_book;
      default:
        return Icons.menu_book;
    }
  }
}
