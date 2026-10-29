# MoodVerses (Flutter Edition)

A beautifully crafted, native-grade Islamic Spiritual Companion developed in **Flutter** with Dark Emerald & Gold aesthetic, authentic Quranic reflections, and the **Waleey (وَلِيّ)** AI Islamic companion.

---

## 🌟 Key Architecture & Features

### 1. 📖 Mood-Based Quran Exploration (`ExploreScreen`)
- **12 Spiritual Emotional States**: Anxiety, Sadness & Grief, Gratitude, Loneliness, Anger & Restraint, Seeking Guidance, Heartbreak, Fear, Seeking Forgiveness, Exhaustion, Seeking Faith, and Quranic Duas.
- Daily contemplation hero cards with one-tap deep reflection.

### 2. 📿 Contemplative Ayah Reader (`AyahReaderScreen`)
- High-fidelity **Arabic Uthmanic typography** with proper RTL formatting.
- Accurate English transliteration and verified translations.
- In-depth spiritual reflection commentaries.
- One-tap copying and favorite toggling with dual local/cloud persistence.

### 3. 🤖 Waleey (وَلِيّ) Islamic AI Companion (`WaleeyScreen`)
- Powered by Google Gemini (`gemini-3.5-flash-lite`) with zero-config out-of-the-box readiness.
- Multi-turn conversational memory with strict user-model alternation.
- Multi-layered token budgeting to prevent mid-sentence cutoffs.
- **Collapsible Chat History Drawer** with auto-summarized session titles.
- **Interactive Citation Cards**: Extracts verified references (Quran, Sahih Bukhari, Muslim, Ibn Kathir) into clickable exploration chips.
- Safe rate-limiting and robust prompt sanitization.

### 4. 📿 Digital Tasbih Counter (`TasbihScreen`)
- Modern Dhikr bead counter with physical haptic tap feedback.
- Preset Dhikr formulas (*SubhanAllah*, *Alhamdulillah*, *Allahu Akbar*, *Astaghfirullah*, *La ilaha illallah*, *Salawat*).
- Dynamic progress ring and cycle celebration cues.

### 5. ☁️ Firebase Authentication & Cloud Sync (`SavedScreen` & `AuthService`)
- **Dual-Layer Architecture**: Instant local offline storage with `SharedPreferences` + seamless Cloud Firestore sync upon sign-in.
- **Authentication Options**:
  - Google Sign-In (100% free with unlimited quota on Spark plan)
  - Email & Password with password reset
  - Mobile Phone SMS OTP
  - Guest mode with zero forced friction
- **Waleey AI Login Prompt Dialog**: Gentle prompt informing users of cross-device chat sync benefits when navigating to Waleey AI.
- **Cloud Status Banners**: Live cloud indicator on the Saved screen and Waleey header.

### 6. 🚀 5-Step First-Install Tour (`OnboardingTourDialog`)
- Automatic walkthrough presented on first launch guiding users through all core tabs and features.

---

## 📁 Project Structure

```
MoodVerses_Flutter/
├── android/                        # Android build scripts, Gradle config, Google Services
│   ├── app/
│   │   ├── google-services.json    # Firebase project configuration
│   │   └── src/main/AndroidManifest.xml
│   └── settings.gradle
├── lib/
│   ├── main.dart                   # App entrypoint & 5-tab navigation bar
│   ├── components/                 # Reusable UI dialogs & components
│   │   ├── auth_dialog.dart        # Phone OTP, Email/Pass, Google Sign-In
│   │   ├── onboarding_tour_dialog.dart  # 5-step interactive onboarding
│   │   └── waleey_login_prompt_dialog.dart # Waleey AI login modal
│   ├── data/
│   │   └── quran_repository.dart   # 40+ authentic verses across 13 moods
│   ├── models/
│   │   ├── chat_message.dart       # Chat session & message models
│   │   └── quran_verse.dart        # Quran verse & mood category schemas
│   ├── screens/
│   │   ├── explore_screen.dart     # Spiritual mood selection
│   │   ├── ayah_reader_screen.dart # Contemplative reader with Uthmanic Arabic
│   │   ├── waleey_screen.dart      # Waleey AI with collapsible history drawer
│   │   ├── tasbih_screen.dart      # Digital Dhikr counter with haptics
│   │   └── saved_screen.dart       # Liked Ayat with cloud status indicator
│   ├── services/
│   │   ├── auth_service.dart       # Firebase Auth wrapper
│   │   ├── chat_repository.dart    # Local & Firestore chat persistence
│   │   ├── favorites_service.dart  # Local & Firestore verse bookmarking
│   │   └── waleey_ai_service.dart  # Gemini AI client with rate limiter & sanitization
│   └── theme/
│       └── app_theme.dart          # Emerald Deep (#051C14) & Gold Accent theme
├── firestore.rules                 # Strict security rules (per-user document isolation)
├── firebase.json                   # Firebase deployment configuration
└── pubspec.yaml                    # Flutter dependencies
```

---

## 🛠️ How to Run

1. Open the project folder in **Android Studio** or **VS Code**:
   `C:\Users\khanZ\AndroidStudioProjects\MoodVerses_Flutter`
2. Fetch dependencies:
   ```bash
   flutter pub get
   ```
3. Connect your Android device or start an emulator.
4. Run the app:
   ```bash
   flutter run
   ```
