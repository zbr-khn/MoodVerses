import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class WaleeyLoginPromptDialog extends StatelessWidget {
  final VoidCallback onDismiss;
  final VoidCallback onSignInClick;

  const WaleeyLoginPromptDialog({
    super.key,
    required this.onDismiss,
    required this.onSignInClick,
  });

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: AppColors.emeraldDeep,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
        side: const BorderSide(color: AppColors.goldAccent, width: 1.5),
      ),
      insetPadding: const EdgeInsets.symmetric(horizontal: 24, vertical: 24),
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Avatar Badge
            Container(
              width: 68,
              height: 68,
              decoration: BoxDecoration(
                color: AppColors.emeraldSurface,
                shape: BoxShape.circle,
                border: Border.all(color: AppColors.goldAccent, width: 1.5),
              ),
              child: const Icon(Icons.auto_awesome, color: AppColors.goldAccent, size: 34),
            ),
            const SizedBox(height: 16),

            const Text(
              "Welcome to Waleey (وَلِيّ)",
              style: TextStyle(
                color: AppColors.textPrimary,
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 4),
            const Text(
              "Your Islamic AI Companion",
              style: TextStyle(
                color: AppColors.goldLight,
                fontSize: 12.5,
                fontWeight: FontWeight.w500,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 14),

            const Text(
              "Sign in to your MoodVerses account to keep your conversations saved across devices and activate cloud sync for your reflections.",
              style: TextStyle(
                color: AppColors.textSecondary,
                fontSize: 12.5,
                height: 1.4,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),

            _benefitRow(Icons.cloud_done, "Save and sync your AI chats across all devices"),
            const SizedBox(height: 8),
            _benefitRow(Icons.security, "Backup your liked Ayat and reflections safely"),

            const SizedBox(height: 20),

            // Buttons
            SizedBox(
              width: double.infinity,
              height: 46,
              child: ElevatedButton(
                onPressed: onSignInClick,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.emeraldPrimary,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
                child: const Text(
                  "Sign In / Register ☁️",
                  style: TextStyle(
                    color: AppColors.emeraldDeep,
                    fontWeight: FontWeight.bold,
                    fontSize: 14,
                  ),
                ),
              ),
            ),
            const SizedBox(height: 6),

            TextButton(
              onPressed: onDismiss,
              child: const Text(
                "Continue as Guest (Local Only)",
                style: TextStyle(color: AppColors.textMuted, fontSize: 12),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _benefitRow(IconData icon, String text) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: AppColors.emeraldSurfaceVariant,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.borderSubtle),
      ),
      child: Row(
        children: [
          Icon(icon, color: AppColors.goldLight, size: 16),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              text,
              style: const TextStyle(color: AppColors.textSecondary, fontSize: 11.5),
            ),
          ),
        ],
      ),
    );
  }
}
