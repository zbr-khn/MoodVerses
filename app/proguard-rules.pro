# Proguard / R8 rules for MoodVerses

# Optimization and Obfuscation
-optimizationpasses 5
-allowaccessmodification
-repackageclasses 'com.moodverses.app.obf'

# Strip debug log outputs in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Keep Compose Runtime
-keep class androidx.compose.** { *; }

# Keep data models used by JSON and serialization
-keepclassmembers class com.moodverses.app.model.** { *; }
-keepclassmembers class com.moodverses.app.ai.ChatMessage { *; }
-keepclassmembers class com.moodverses.app.ai.KnowledgeEntry { *; }
