import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class PrivacyPolicyScreen extends StatelessWidget {
  const PrivacyPolicyScreen({super.key});

  static void show(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(builder: (ctx) => const PrivacyPolicyScreen()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.emeraldDeep,
      appBar: AppBar(
        title: const Text(
          "Privacy Policy",
          style: TextStyle(color: AppColors.textPrimary, fontWeight: FontWeight.bold, fontSize: 18),
        ),
        backgroundColor: AppColors.emeraldDeep,
        iconTheme: const IconThemeData(color: AppColors.goldAccent),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
        child: Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(
            color: AppColors.emeraldSurface,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: AppColors.borderSubtle),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                "MoodVerses Privacy Policy",
                style: TextStyle(color: AppColors.goldLight, fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 6),
              const Text(
                "Effective Date: November 2026",
                style: TextStyle(color: AppColors.textMuted, fontSize: 12),
              ),
              const SizedBox(height: 18),
              const Divider(color: AppColors.borderSubtle),
              const SizedBox(height: 16),

              _buildSection(
                title: "1. Offline-First Privacy",
                content: "MoodVerses is built with an offline-first philosophy. When using the app as a guest, zero personal information is collected, tracked, or transmitted to any server. Your saved favorite Ayat, custom profile name, and Tasbih counts remain exclusively on your device.",
              ),
              _buildSection(
                title: "2. Optional Cloud Synchronization",
                content: "If you choose to sign in (via Google Sign-In, Email/Password, or Phone OTP), authentication is handled securely by Google Firebase Authentication. Your email or phone number is stored to protect your session and sync your saved favorites across your devices.",
              ),
              _buildSection(
                title: "3. Spiritual Inquiries & Waleey AI",
                content: "When consulting Waleey, queries are transmitted over encrypted TLS connections solely to retrieve authentic Islamic citations from the Quran and Sunnah. Queries are never sold, never shared with third-party data brokers, and never used to build advertising profiles.",
              ),
              _buildSection(
                title: "4. No Advertising or Tracking SDKs",
                content: "MoodVerses contains no ad networks, tracking cookies, or commercial telemetry SDKs. The app exists purely for authentic Islamic spiritual reflection and contemplation.",
              ),
              _buildSection(
                title: "5. Data Control & Deletion",
                content: "You retain full control over your data. You may clear your local favorites and conversations at any time. If you wish to delete your cloud account completely, you may do so by reaching out via our official repository.",
              ),
              _buildSection(
                title: "6. Contact",
                content: "For inquiries regarding your data or security, visit our official repository: github.com/zbr-khn/MoodVerses.",
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSection({required String title, required String content}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 18),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(color: AppColors.goldAccent, fontSize: 14, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 6),
          Text(
            content,
            style: const TextStyle(color: AppColors.textSecondary, fontSize: 13, height: 1.55),
          ),
        ],
      ),
    );
  }
}
