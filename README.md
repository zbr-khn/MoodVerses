# MoodVerses 2.0 (آيات السكينة) 🌿
### Native Islamic Android Application with Jetpack Compose & Waleey AI Companion

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose_Material_3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![AI](https://img.shields.io/badge/AI-Waleey_Gemini_3.5-F59E0B?style=for-the-badge&logo=google&logoColor=white)

---

## 📖 Overview

**MoodVerses 2.0** is a complete, from-the-ground-up native rewrite of the MoodVerses concept. Designed with a calming Emerald & Gold aesthetic, the app connects users with Allah's words based on how their heart is feeling, offering authentic Quranic verses, spiritual reflections, and an interactive Islamic AI companion.

---

## ✨ Key Features in Version 2.0

### 1. 🌙 Mood-Based Quran Exploration
- **12 Spiritual Mood States**:
  - Anxiety & Overthinking (السكينة والطمأنينة)
  - Sadness & Grief (تفريج الكرب والحزن)
  - Inner Peace (الطمأنينة وراحة البال)
  - Gratitude & Joy (الشكر والحمد)
  - Strength & Patience (الصبر والثبات)
  - Seeking Guidance (طلب الهداية والرشاد)
  - Hope & Renewal (الرجاء وحسن الظن)
  - Forgiveness & Repentance (المغفرة والتوبة)
  - Loneliness (الأنس بالله)
  - Love of Allah (حب الله والقرب منه)
  - Calming Anger (كظم الغيظ والحلم)
  - Morning & Evening Remembrance (أذكار الصباح والمساء)
- **Authentic Verses**: Every Ayah includes the original Uthmanic Arabic text, accurate transliteration, verified English translation, Surah & Ayah reference, and thematic context.
- **Pure Contemplation**: Removed distracting numbers and dataset counters (`"1 of 40"`), replacing them with elegant Arabic spiritual mood badges.

### 2. 🌟 Waleey (وَلِيّ) — Your Islamic AI Companion
- **Real-Time Islamic Knowledge & History**:
  - Milestones of Prophetic Seerah (Ta'if, Badr, Hudaybiyyah, Hijrah).
  - History of the Holy Quran & Hadith verification (Zaid ibn Thabit, Caliphs, Imam Bukhari).
  - The Islamic Golden Age & pioneers (House of Wisdom, Al-Khwarizmi, Ibn Sina, Fatima al-Fihri).
  - Daily worship guidance (developing Khushu' in Salah, Tahajjud, accepted Dua, Tawbah).
- **Dual-Engine Architecture**:
  - **Live AI Mode**: Powered by Google Gemini with verified citations and structured Markdown formatting.
  - **Offline Fallback**: Curated knowledge repository that works without internet or API keys.
- **Safety & Flood Protection**: Integrated client-side rate limiting (max 8 requests/minute with cooldowns) and strict input sanitization.

### 3. 📿 Digital Tasbih Counter
- Smooth interactive Dhikr counter with haptic vibration feedback.
- Preset Dhikr (SubhanAllah, Alhamdulillah, Allahu Akbar, Astaghfirullah).
- Quick reset and cycle-completion indicators.

### 4. 💖 Saved Favorites
- Bookmark meaningful Ayats for daily reflection and offline contemplation.

---

## 🛡️ Architecture & Security Hardening

- **Jetpack Compose + Material 3**: Fluid animations, edge-to-edge layout, and adaptive Dark Emerald palette.
- **Network Security Configuration**: Strict HTTPS enforcement (`cleartextTrafficPermitted="false"`).
- **Data Protection**: `android:allowBackup="false"` to prevent unauthorized ADB data extraction.
- **R8 / ProGuard Minification**: Code obfuscation and automated diagnostic log stripping in release builds.
- **Zero-Config AI Service**: Waleey AI runs completely out-of-the-box — users never need to provide, manage, or configure any API keys.

---

## 🚀 Building the Project

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 36 (minSdk 24)
- JDK 17 or Android Studio's bundled JBR

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/zbr-khn/MoodVerses.git
   ```
2. Open the project in Android Studio.
3. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📜 Version History
- **v2.0.0 (Current)**: Complete native Android rewrite using Kotlin & Jetpack Compose, added Waleey AI Companion, expanded authentic Quranic database, removed counters, enhanced UI/UX, and added full security hardening.
- **v1.0.0 (Legacy)**: Initial hybrid Cordova web app version (archived in branch `v1-legacy`).
