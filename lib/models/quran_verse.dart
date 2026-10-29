class MoodCategory {
  final String id;
  final String name;
  final String arabicName;
  final String icon;
  final String description;
  final int colorHex;

  const MoodCategory(
    this.id,
    this.name,
    this.arabicName,
    this.icon,
    this.description,
    this.colorHex,
  );
}

class QuranVerse {
  final String id;
  final String mood;
  final String moodLabel;
  final String arabicMood;
  final String surahName;
  final int surahNumber;
  final String ayahNumber;
  final String reference;
  final String arabicText;
  final String transliteration;
  final String translation;
  final String reflection;
  final List<String> tags;

  const QuranVerse({
    required this.id,
    required this.mood,
    required this.moodLabel,
    required this.arabicMood,
    required this.surahName,
    required this.surahNumber,
    required this.ayahNumber,
    required this.reference,
    required this.arabicText,
    required this.transliteration,
    required this.translation,
    required this.reflection,
    required this.tags,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'mood': mood,
      'moodLabel': moodLabel,
      'arabicMood': arabicMood,
      'surahName': surahName,
      'surahNumber': surahNumber,
      'ayahNumber': ayahNumber,
      'reference': reference,
      'arabicText': arabicText,
      'transliteration': transliteration,
      'translation': translation,
      'reflection': reflection,
      'tags': tags,
    };
  }

  factory QuranVerse.fromMap(Map<String, dynamic> map) {
    return QuranVerse(
      id: map['id'] ?? '',
      mood: map['mood'] ?? '',
      moodLabel: map['moodLabel'] ?? '',
      arabicMood: map['arabicMood'] ?? '',
      surahName: map['surahName'] ?? '',
      surahNumber: map['surahNumber'] ?? 1,
      ayahNumber: map['ayahNumber'] ?? '1',
      reference: map['reference'] ?? '',
      arabicText: map['arabicText'] ?? '',
      transliteration: map['transliteration'] ?? '',
      translation: map['translation'] ?? '',
      reflection: map['reflection'] ?? '',
      tags: List<String>.from(map['tags'] ?? []),
    );
  }
}
