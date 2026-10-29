import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../theme/app_theme.dart';

class OnboardingManager {
  static const String _key = 'has_seen_onboarding_v2';

  static Future<bool> hasCompletedTour() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getBool(_key) ?? false;
  }

  static Future<void> markCompleted() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(_key, true);
  }
}

class OnboardingStep {
  final String title;
  final String arabicTitle;
  final String description;
  final IconData icon;
  final Color iconColor;
  final String tip;

  const OnboardingStep({
    required this.title,
    required this.arabicTitle,
    required this.description,
    required this.icon,
    required this.iconColor,
    required this.tip,
  });
}

class OnboardingTourDialog extends StatefulWidget {
  final VoidCallback onDismiss;

  const OnboardingTourDialog({super.key, required this.onDismiss});

  @override
  State<OnboardingTourDialog> createState() => _OnboardingTourDialogState();
}

class _OnboardingTourDialogState extends State<OnboardingTourDialog> {
  int _currentIndex = 0;

  final List<OnboardingStep> _steps = const [
    OnboardingStep(
      title: "Mood-Based Quran Exploration",
      arabicTitle: "الاستكشاف حسب المشاعر",
      description: "Discover authentic Quranic verses curated for 12 spiritual emotional states — including Anxiety, Sadness, Gratitude, Loneliness, Seeking Guidance, and Heartbreak.",
      icon: Icons.explore,
      iconColor: AppColors.emeraldPrimary,
      tip: "💡 Tap any mood card on the Explore tab to dive into soothing Ayat for that feeling.",
    ),
    OnboardingStep(
      title: "Contemplative Ayah Reader",
      arabicTitle: "تلاوة وتدبر الآيات",
      description: "Immerse yourself in authentic Uthmanic Arabic calligraphy, accurate English transliterations, verified translations, and spiritual reflections.",
      icon: Icons.menu_book,
      iconColor: AppColors.goldLight,
      tip: "💡 Tap the Heart icon on any verse to save it into your favorites collection.",
    ),
    OnboardingStep(
      title: "Waleey AI Companion",
      arabicTitle: "وَلِيّ — رفيقك الإسلامي الذكي",
      description: "Ask questions about the Holy Quran, Prophetic Seerah (Ta'if, Badr, Hijrah), Islamic Golden Age scholars, and Salah guidance. Waleey provides verified citations.",
      icon: Icons.auto_awesome,
      iconColor: AppColors.goldAccent,
      tip: "💡 Every citation is interactive — tap any reference to explore its full context and commentary.",
    ),
    OnboardingStep(
      title: "Digital Tasbih Counter",
      arabicTitle: "المسبحة الإلكترونية والأذكار",
      description: "Recite daily Dhikr (SubhanAllah, Alhamdulillah, Allahu Akbar, Astaghfirullah) with physical haptic vibration and tap feedback on every bead.",
      icon: Icons.touch_app,
      iconColor: AppColors.emeraldPrimary,
      tip: "💡 Complete 33 or 100 beads to celebrate cycle completions with vibration cues.",
    ),
    OnboardingStep(
      title: "Cross-Device Cloud Sync",
      arabicTitle: "المزامنة السحابية والمحفوظات",
      description: "Log in with your Mobile Phone OTP, Email, or Google account. Your liked Ayat and Waleey AI conversations will back up and sync across all your devices automatically!",
      icon: Icons.cloud_done,
      iconColor: AppColors.goldAccent,
      tip: "💡 You can always use the app offline as a guest — cloud sync activates whenever you log in.",
    ),
  ];

  void _finish() {
    OnboardingManager.markCompleted();
    widget.onDismiss();
  }

  @override
  Widget build(BuildContext context) {
    final step = _steps[_currentIndex];
    final isLast = _currentIndex == _steps.length - 1;

    return Dialog(
      backgroundColor: AppColors.emeraldDeep,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
        side: const BorderSide(color: AppColors.goldAccent, width: 1.5),
      ),
      insetPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header Row
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(
                    color: AppColors.emeraldSurfaceVariant,
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(color: AppColors.borderSubtle),
                  ),
                  child: Text(
                    "Step ${_currentIndex + 1} of ${_steps.length}",
                    style: const TextStyle(
                      color: AppColors.goldLight,
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                TextButton(
                  onPressed: _finish,
                  child: const Text(
                    "Skip Tour",
                    style: TextStyle(color: AppColors.textMuted, fontSize: 12),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // Icon Badge
            Container(
              width: 76,
              height: 76,
              decoration: BoxDecoration(
                color: AppColors.emeraldSurface,
                shape: BoxShape.circle,
                border: Border.all(color: step.iconColor, width: 2),
              ),
              child: Icon(step.icon, color: step.iconColor, size: 38),
            ),
            const SizedBox(height: 14),

            // Titles
            Text(
              step.arabicTitle,
              style: const TextStyle(
                color: AppColors.goldAccent,
                fontSize: 15,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 4),
            Text(
              step.title,
              style: const TextStyle(
                color: AppColors.textPrimary,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 12),

            // Description
            Text(
              step.description,
              style: const TextStyle(
                color: AppColors.textSecondary,
                fontSize: 13,
                height: 1.4,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 14),

            // Tip Box
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: AppColors.emeraldSurfaceVariant,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: AppColors.borderSubtle),
              ),
              child: Text(
                step.tip,
                style: const TextStyle(
                  color: AppColors.goldLight,
                  fontSize: 11.5,
                  height: 1.3,
                ),
                textAlign: TextAlign.center,
              ),
            ),
            const SizedBox(height: 20),

            // Dots Indicator
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(_steps.length, (index) {
                final isSelected = index == _currentIndex;
                return AnimatedContainer(
                  duration: const Duration(milliseconds: 250),
                  margin: const EdgeInsets.symmetric(horizontal: 4),
                  width: isSelected ? 22 : 6,
                  height: 6,
                  decoration: BoxDecoration(
                    color: isSelected ? AppColors.goldAccent : Colors.grey.withValues(alpha: 0.4),
                    borderRadius: BorderRadius.circular(3),
                  ),
                );
              }),
            ),
            const SizedBox(height: 20),

            // Action Buttons
            Row(
              children: [
                if (_currentIndex > 0) ...[
                  Expanded(
                    child: OutlinedButton(
                      onPressed: () => setState(() => _currentIndex--),
                      style: OutlinedButton.styleFrom(
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        side: const BorderSide(color: AppColors.borderSubtle),
                      ),
                      child: const Text("Back", style: TextStyle(color: AppColors.textSecondary)),
                    ),
                  ),
                  const SizedBox(width: 10),
                ],
                Expanded(
                  flex: _currentIndex > 0 ? 2 : 1,
                  child: ElevatedButton(
                    onPressed: () {
                      if (isLast) {
                        _finish();
                      } else {
                        setState(() => _currentIndex++);
                      }
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.emeraldPrimary,
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      padding: const EdgeInsets.symmetric(vertical: 12),
                    ),
                    child: Text(
                      isLast ? "Get Started 🚀" : "Next",
                      style: const TextStyle(
                        color: AppColors.emeraldDeep,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
