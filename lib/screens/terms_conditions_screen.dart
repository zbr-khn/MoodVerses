import 'package:flutter/material.dart';
import '../theme/app_theme.dart';

class TermsConditionsScreen extends StatelessWidget {
  const TermsConditionsScreen({super.key});

  static void show(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(builder: (ctx) => const TermsConditionsScreen()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.emeraldDeep,
      appBar: AppBar(
        title: const Text(
          "Terms and Conditions",
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
                "MoodVerses Terms and Conditions",
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
                title: "1. Acceptance of Terms",
                content: "By downloading, accessing, or using MoodVerses, you agree to comply with and be bound by these Terms and Conditions. If you do not agree to these terms, please discontinue use of the application.",
              ),
              _buildSection(
                title: "2. Scope & Purpose",
                content: "MoodVerses provides thematic Quranic verse selections, authentic translations, reflections, a digital Tasbih Dhikr counter, and conversational Islamic knowledge lookup through Waleey. All content is intended for devotional, contemplative, and educational purposes.",
              ),
              _buildSection(
                title: "3. Religious Content & Scholarly Disclaimer",
                content: "All Quranic Arabic texts and standard English translations are compiled from verified, scholarly recognized classical sources. Reflections and AI assistance are designed to aid personal contemplation. They do not constitute formal religious rulings (Fatwas). For complex theological or jurisprudence (Fiqh) matters, users should consult qualified Islamic scholars.",
              ),
              _buildSection(
                title: "4. User Conduct",
                content: "Users agree to use MoodVerses with dignity and respect. Any attempt to abuse the service, disrupt server infrastructure, or use the app for unlawful purposes is strictly prohibited.",
              ),
              _buildSection(
                title: "5. Intellectual Property",
                content: "The Holy Quran is the heritage of the entire Muslim Ummah. The original user interface, design, typography, software architecture, and codebase of MoodVerses are protected by intellectual property rights.",
              ),
              _buildSection(
                title: "6. Limitation of Liability",
                content: "MoodVerses is provided on an 'as is' and 'as available' basis. While we strive for uncompromising accuracy and uptime, we make no guarantees that service will be free from temporary interruptions.",
              ),
              _buildSection(
                title: "7. Contact",
                content: "For questions or suggestions regarding these terms, visit github.com/zbr-khn/MoodVerses.",
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
