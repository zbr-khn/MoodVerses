package com.moodverses.app.model

data class QuranVerse(
    val id: String,
    val mood: String,
    val moodLabel: String,
    val arabicMood: String,
    val surahName: String,
    val surahNumber: Int,
    val ayahNumber: String,
    val reference: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val reflection: String,
    val tags: List<String> = emptyList()
)

data class MoodCategory(
    val id: String,
    val label: String,
    val arabicLabel: String,
    val emoji: String,
    val subtitle: String,
    val colorHex: Long
)
