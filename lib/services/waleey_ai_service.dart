import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../models/chat_message.dart';

class KnowledgeEntry {
  final String id;
  final String title;
  final List<String> keywords;
  final String answer;
  final List<String> citations;
  final List<String> suggestions;

  const KnowledgeEntry({
    required this.id,
    required this.title,
    required this.keywords,
    required this.answer,
    required this.citations,
    required this.suggestions,
  });
}

class WaleeyAiService {
  // Configurable Gemini API key via --dart-define=GEMINI_API_KEY=...
  static const String _geminiApiKey = String.fromEnvironment(
    'GEMINI_API_KEY',
    defaultValue: '',
  );

  static const List<String> starterPrompts = [
    "Prophet's ﷺ Mercy at Ta'if",
    "How the Quran was compiled",
    "Attaining Khushu in Salah",
    "Virtues of Tahajjud",
    "Islamic Golden Age & Scholars",
    "Overcoming anxiety & grief",
    "The 4 Caliphs (Khulafa)",
    "Sincere Repentance (Tawbah)"
  ];

  // Authentic Pre-compiled Islamic Knowledge Base
  static const List<KnowledgeEntry> knowledgeBase = [
    KnowledgeEntry(
      id: "taif_mercy",
      title: "The Prophet's ﷺ Mercy at Ta'if",
      keywords: ["taif", "mercy", "stoned", "angels", "patience", "forgiveness", "mountain", "suffering"],
      answer: "In the 10th year of Prophethood (the Year of Sorrow), after losing both Khadijah (RA) and Abu Talib, Prophet Muhammad ﷺ walked to Ta'if to invite the Thaqif tribe to Islam.\n\n"
          "Instead of welcoming him, the leaders set upon him street youth who pelted him with stones until his sandals were soaked with blood. Seeking refuge in an orchard, he made his famous supplication:\n"
          "*\"O Allah, unto You do I complain of my weakness, of my helplessness, and of my insignificance before people...\"*\n\n"
          "The Angel Jibreel (AS) descended alongside the Angel of Mountains, who asked permission to crush the city between the two surrounding mountains (Al-Akhshabayn).\n\n"
          "The Prophet ﷺ replied with timeless mercy:\n"
          "*\"No, rather I hope that Allah will bring forth from their loins descendants who will worship Allah alone without associating any partners with Him.\"*\n\n"
          "✨ **Lesson**: Never let the cruelty of others extinguish your compassion. Even in his deepest pain, Rasulullah ﷺ prayed for the guidance of his persecutors.",
      citations: ["Sahih al-Bukhari 3231", "Sahih Muslim 1795"],
      suggestions: ["Conquest of Makkah", "The Year of Sorrow", "Treaty of Hudaybiyyah"],
    ),
    KnowledgeEntry(
      id: "quran_compilation",
      title: "Compilation of the Holy Quran",
      keywords: ["quran", "compilation", "compiled", "uthman", "abu bakr", "zaid ibn thabit", "mushaf", "manuscript", "preserved"],
      answer: "The preservation and compilation of the Quran unfolded through three meticulously documented historical stages:\n\n"
          "1. **During the Lifetime of the Prophet ﷺ (Oral & Inscribed Records)**:\n"
          "Whenever an Ayah was revealed, the Prophet ﷺ designated official scribes (Kuttab al-Wahy)—including Zaid ibn Thabit, Ali ibn Abi Talib, and Ubayy ibn Ka'b—to write it down on parchment, leather, and palm leaves, while thousands of Sahabah memorized every verse.\n\n"
          "2. **The Era of Caliph Abu Bakr As-Siddiq (RA)**:\n"
          "Following the Battle of Yamama (632 CE) where many Huffaz were martyred, Umar (RA) proposed gathering all manuscripts into a single codex. Abu Bakr appointed Zaid ibn Thabit (RA) to lead the task with two eyewitnesses required for every written verse. This primary collection was safeguarded with Hafsah bint Umar (RA).\n\n"
          "3. **The Era of Caliph Uthman ibn Affan (RA) (Standardization)**:\n"
          "In 650 CE, Caliph Uthman (RA) formed a committee headed by Zaid ibn Thabit to transcribe standard copies based on the original Quraysh dialect, sending copies to major capitals.\n\n"
          "✨ **Divine Guarantee**: *\"Indeed, it is We who sent down the Qur'an and indeed, We will be its guardian.\"* [Surah Al-Hijr 15:9]",
      citations: ["Surah Al-Hijr 15:9", "Sahih al-Bukhari 4986", "Sahih al-Bukhari 4987"],
      suggestions: ["Who was Zaid ibn Thabit?", "The 4 Caliphs (Khulafa)", "Surah Al-Kahf background"],
    ),
    KnowledgeEntry(
      id: "khushu_salah",
      title: "Attaining Khushu' (Devotion) in Salah",
      keywords: ["khushu", "focus", "salah", "prayer", "distraction", "mindfulness", "concentrate", "presence"],
      answer: "Khushu' (خُشُوع) is the humble presence of heart and stillness of mind in prayer. The Quran describes the believers:\n"
          "*\"Successful indeed are the believers: those who are humble in their prayers.\"* [Surah Al-Mu'minun 23:1-2]\n\n"
          "Practical steps to nurture Khushu':\n"
          "1. **Pre-Prayer Transition (Al-Wuquf)**: Don't rush into Salah directly from checking your phone. Perform Wudu mindfully, feeling sins wash away.\n"
          "2. **Understand What You Recite**: Reflect upon Al-Fatihah. Remember that Allah responds to you personally verse by verse in the Hadith Qudsi.\n"
          "3. **Pace and Tranquility (Tuma'ninah)**: Give every posture (Ruku', Sujud, Standing) its full right. Pause for a breath after each motion.\n"
          "4. **Treat Each Prayer as Your Last**: The Prophet ﷺ advised: *\"When you stand for prayer, pray like a person saying their farewell.\"*\n"
          "5. **Seek Refuge from Waswas**: If wandering thoughts invade, recite *A'udhu billahi minash-shaytanir-rajim* quietly.",
      citations: ["Surah Al-Mu'minun 23:1-2", "Sunan Ibn Majah 4171", "Sahih Muslim 2203"],
      suggestions: ["Virtues of Tahajjud", "Conditions of accepted Dua", "Overcoming anxiety & grief"],
    ),
    KnowledgeEntry(
      id: "tahajjud_virtues",
      title: "The Virtues & Beauty of Tahajjud (Night Prayer)",
      keywords: ["tahajjud", "night prayer", "qiyam", "last third", "waking up", "night", "dua at night"],
      answer: "The Night Prayer (Tahajjud / Qiyam al-Layl) is the hallmark of the righteous and the most intimate conversation between a servant and the Creator.\n\n"
          "Allah subhanahu wa ta'ala says:\n"
          "*\"And from [part of] the night, pray with it as additional [worship] for you; it is expected that your Lord will raise you to a praised station.\"* [Surah Al-Isra 17:79]\n\n"
          "Key virtues of Tahajjud:\n"
          "- **Divine Descent in the Last Third**: Rasulullah ﷺ said: *\"Our Lord descends every night to the lowest heaven when the last third remains, saying: 'Who calls upon Me so that I may answer him? Who asks of Me that I may grant him? Who seeks My forgiveness that I may forgive him?'\"* [Bukhari]\n"
          "- **The Arrow that Never Misses**: Imam Ash-Shafi'i (RA) said: *\"The Dua made at Tahajjud is like an arrow that does not miss its target.\"*\n"
          "- **Inner Light & Peace**: Al-Hasan Al-Basri said: *\"They were alone with the Most Merciful, so He adorned them with His light.\"*\n\n"
          "**How to Pray**: Wake up in the last third of the night, perform Wudu, and pray 2 rak'ahs at a time with calm reflection, followed by the Witr prayer.",
      citations: ["Surah Al-Isra 17:79", "Sahih al-Bukhari 1145", "Sahih Muslim 758"],
      suggestions: ["Conditions of accepted Dua", "Attaining Khushu in Salah", "Sincere Repentance (Tawbah)"],
    ),
    KnowledgeEntry(
      id: "islamic_golden_age",
      title: "The Islamic Golden Age & Pioneering Scholars",
      keywords: ["golden age", "science", "scholars", "bayt al-hikmah", "khwarizmi", "ibn sina", "optics", "algebra", "history", "civilization"],
      answer: "From the mid-8th to 14th century, the Islamic Golden Age transformed world history by synthesizing science, philosophy, and spirituality:\n\n"
          "1. **House of Wisdom (Bayt al-Hikmah) in Baghdad**:\n"
          "Founded under the Abbasid Caliphs, it served as the world's greatest center of translation, astronomy, and mathematics.\n\n"
          "2. **Muhammad ibn Musa Al-Khwarizmi (780–850 CE)**:\n"
          "- Invented **Algebra** (*Al-Jabr*). The word **Algorithm** originates directly from his name.\n\n"
          "3. **Al-Hasan Ibn al-Haytham (Alhazen) (965–1040 CE)**:\n"
          "- Father of **Modern Optics** and pioneer of the **Scientific Method** with *Kitab al-Manazir*.\n\n"
          "4. **Ibn Sina (Avicenna) (980–1037 CE)**:\n"
          "- Authored *The Canon of Medicine*, standard in Europe for 600 years.\n\n"
          "5. **Fatima al-Fihri (859 CE)**:\n"
          "- Founded the **University of al-Qarawiyyin** in Morocco—the world's oldest university.",
      citations: ["Surah Yunus 10:101", "Surah Ali 'Imran 3:190-191"],
      suggestions: ["The 4 Caliphs (Khulafa)", "Who was Imam Bukhari?", "How the Quran was compiled"],
    ),
    KnowledgeEntry(
      id: "anxiety_grief",
      title: "Overcoming Anxiety, Sadness & Grief in Islam",
      keywords: ["anxiety", "depression", "grief", "sadness", "stress", "worry", "trials", "hardship", "sabr", "tawakkul", "pain"],
      answer: "In Islam, sadness and worry are not marks of spiritual failure. Even the greatest Prophets wept:\n"
          "- Prophet Ya'qub (AS) wept for Yusuf (AS) until his eyesight dimmed.\n"
          "- Prophet Muhammad ﷺ named the year he lost Khadijah (RA) and Abu Talib *\"The Year of Sorrow\"*.\n\n"
          "Islamic pillars for emotional healing:\n"
          "1. **Resilience & Sabr**: *\"And seek help through patience and prayer...\"* [Surah Al-Baqarah 2:45]\n"
          "2. **Every Pain Expiates Sins**: *\"No fatigue, illness, anxiety, or sorrow befalls a Muslim—even the prick of a thorn—except that Allah expiates some of their sins through it.\"* [Bukhari]\n"
          "3. **The Prophetic Du'a for Anxiety**:\n"
          "*«اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ»*\n"
          "*\"O Allah, I seek refuge in You from anxiety and grief, helplessness and laziness, stinginess and cowardice, debt, and being overpowered by people.\"* [Bukhari 2893]\n"
          "4. **Relief Coupled with Hardship**: *\"Indeed, with hardship comes ease.\"* [Surah Ash-Sharh 94:6]",
      citations: ["Surah Ash-Sharh 94:5-6", "Surah Al-Baqarah 2:45", "Sahih al-Bukhari 2893"],
      suggestions: ["Prophet's ﷺ Mercy at Ta'if", "Conditions of accepted Dua", "Virtues of Tahajjud"],
    ),
    KnowledgeEntry(
      id: "four_caliphs",
      title: "The Rightly Guided Caliphs (Al-Khulafa ar-Rashidun)",
      keywords: ["caliphs", "khulafa", "abu bakr", "umar", "uthman", "ali", "rashidun", "companions", "sahabah"],
      answer: "The four Rightly Guided Caliphs guided the Ummah after the Prophet ﷺ (632–661 CE):\n\n"
          "1. **Abu Bakr As-Siddiq (RA)**: Epitome of steadfast faith (*Siddiq*). Unified the community and initiated Quran compilation.\n"
          "2. **Umar ibn Al-Khattab (RA)**: Giant of justice (*Al-Faruq*). Established the Hijri calendar and public welfare systems.\n"
          "3. **Uthman ibn Affan (RA)**: Possessor of the Two Lights (*Dhu an-Nurayn*). Renowned for generosity and standardizing the Mus'haf.\n"
          "4. **Ali ibn Abi Talib (RA)**: The Lion of Allah (*Asadullah*). Famed for immense knowledge, wisdom, and steadfast justice.",
      citations: ["Sunan Abi Dawud 4607", "Jami` at-Tirmidhi 3713"],
      suggestions: ["How the Quran was compiled", "Islamic Golden Age & Scholars", "Prophet's ﷺ Mercy at Ta'if"],
    ),
    KnowledgeEntry(
      id: "tawbah_repentance",
      title: "Sincere Repentance (Tawbah) and Allah's Infinite Mercy",
      keywords: ["tawbah", "repentance", "sin", "forgiveness", "guilt", "mercy", "turn back", "istighfar"],
      answer: "In Islam, no sin is beyond Allah's mercy. Despair of His forgiveness is forbidden:\n\n"
          "*\"Say, 'O My servants who have transgressed against themselves, do not despair of the mercy of Allah. Indeed, Allah forgives all sins. Indeed, it is He who is the Forgiving, the Merciful.'\"* [Surah Az-Zumar 39:53]\n\n"
          "The 4 Conditions of Sincere Repentance:\n"
          "1. **Immediate Cessation**: Stop the wrongdoing immediately.\n"
          "2. **Genuine Remorse (An-Nadam)**: Feeling deep regret in the heart (*\"Regret is repentance\"*).\n"
          "3. **Firm Intention**: Resolving sincerely never to return to the sin.\n"
          "4. **Restitution of Rights**: Returning any rights or apologizing if another person was wronged.\n\n"
          "✨ *\"The one who repents from sin is like one who has no sin at all.\"* [Ibn Majah 4250]",
      citations: ["Surah Az-Zumar 39:53", "Sunan Ibn Majah 4250", "Sahih Muslim 2749"],
      suggestions: ["Virtues of Tahajjud", "Conditions of accepted Dua", "Attaining Khushu in Salah"],
    ),
  ];

  // Rate Limiting
  static DateTime _lastRequestTime = DateTime.fromMillisecondsSinceEpoch(0);
  static final List<DateTime> _requestTimestamps = [];
  static const int _maxRequestsPerMinute = 8;
  static const int _minIntervalMs = 2500;

  static bool _checkRateLimit() {
    final now = DateTime.now();
    if (now.difference(_lastRequestTime).inMilliseconds < _minIntervalMs) {
      return false;
    }
    _requestTimestamps.removeWhere((t) => now.difference(t).inSeconds > 60);
    if (_requestTimestamps.length >= _maxRequestsPerMinute) {
      return false;
    }
    _lastRequestTime = now;
    _requestTimestamps.add(now);
    return true;
  }

  static String sanitizeInput(String input) {
    return input
        .replaceAll(RegExp(r'<[^>]*>'), '')
        .replaceAll(RegExp(r'javascript:', caseSensitive: false), '')
        .replaceAll(RegExp(r'data:text/html', caseSensitive: false), '')
        .replaceAll(RegExp(r'on\w+\s*=', caseSensitive: false), '')
        .trim();
  }

  static List<String> extractAuthenticCitations(String text) {
    final citations = <String>[];
    final patterns = [
      RegExp(r'(?:Surah|Surat)\s+[A-Za-z\x27\-]+(?:\s+[A-Za-z\x27\-]+)?\s+\d+:\d+(?:-\d+)?', caseSensitive: false),
      RegExp(r'(?:Sahih\s+al-Bukhari|Sahih\s+Muslim|Sunan\s+Abi\s+Dawud|Jami\x27?\s+at-Tirmidhi|Sunan\s+an-Nasa\x27i|Sunan\s+Ibn\s+Majah|Muwatta\s+Malik|Musnad\s+Ahmad)\s+\d+', caseSensitive: false),
    ];

    for (final pattern in patterns) {
      for (final match in pattern.allMatches(text)) {
        final citation = match.group(0)?.trim();
        if (citation != null && !citations.contains(citation) && citations.length < 4) {
          citations.add(citation);
        }
      }
    }
    return citations;
  }

  static KnowledgeEntry? _findKnowledgeEntry(String cleanQuery) {
    // 1. Direct title or prompt matching
    for (final entry in knowledgeBase) {
      if (cleanQuery.contains(entry.id) ||
          cleanQuery.contains(entry.title.toLowerCase()) ||
          entry.title.toLowerCase().contains(cleanQuery)) {
        return entry;
      }
    }

    // 2. Keyword matching
    KnowledgeEntry? bestMatch;
    int maxMatches = 0;
    for (final entry in knowledgeBase) {
      int count = 0;
      for (final kw in entry.keywords) {
        if (cleanQuery.contains(kw)) {
          count++;
        }
      }
      if (count > maxMatches) {
        maxMatches = count;
        bestMatch = entry;
      }
    }

    if (maxMatches >= 2) {
      return bestMatch;
    }
    return null;
  }

  static Future<ChatMessage> processQuery(
    String query, {
    List<ChatMessage> history = const [],
    String? userName,
  }) async {
    final sanitized = sanitizeInput(query);
    if (sanitized.isEmpty) {
      return ChatMessage(text: "Please ask a question about Islamic knowledge.", isUser: false);
    }

    if (!_checkRateLimit()) {
      return ChatMessage(
        text: "Please pause for a brief moment before sending another inquiry.\n\n*«وَاصْبِرْ فَإِنَّ اللَّهَ لَا يُضِيعُ أَجْرَ الْمُحْسِنِينَ»*\n\n*\"And be patient, for indeed Allah does not allow the reward of the doers of good to be lost.\"* [Surah Hud 11:115]",
        isUser: false,
        suggestions: List.from(starterPrompts)..shuffle(),
      );
    }

    final clean = sanitized.toLowerCase();
    final nameGreeting = (userName != null && userName.trim().isNotEmpty) ? ", ${userName.trim()}" : "";

    // 1. Common greetings
    if (["salam", "assalam", "assalamu alaikum", "hello", "hi"].contains(clean)) {
      return ChatMessage(
        text: "Wa Alaikum Assalam wa Rahmatullahi wa Barakatuh$nameGreeting! 🌸 May peace and blessings of Allah be upon you. How may I assist your reflection today?",
        isUser: false,
        suggestions: List.from(starterPrompts)..shuffle(),
      );
    }

    // 2. Check authentic verified local knowledge base (instant response with citations)
    final matched = _findKnowledgeEntry(clean);
    if (matched != null) {
      return ChatMessage(
        text: matched.answer,
        isUser: false,
        citations: matched.citations,
        suggestions: matched.suggestions,
      );
    }

    // 3. Call Live Gemini API for custom queries
    final liveResponse = await _callGeminiApi(sanitized, history, userName: userName);
    if (liveResponse != null) {
      return liveResponse;
    }

    // 4. Offline Fallback
    return ChatMessage(
      text: "Seeking knowledge (*Talab al-'Ilm*) is a sacred pursuit in Islam.\n\n"
          "I am momentarily unable to reach the live network to generate a complete answer for: *\"$sanitized\"*.\n\n"
          "Please check your internet connection, or explore one of the topics below:",
      isUser: false,
      citations: const [],
      suggestions: List.from(starterPrompts)..shuffle(),
    );
  }

  static Future<ChatMessage?> _callGeminiApi(
    String prompt,
    List<ChatMessage> history, {
    String? userName,
  }) async {
    final models = ["gemini-3.5-flash-lite", "gemini-3.8-flash", "gemini-flash-latest"];

    for (final model in models) {
      try {
        final url = Uri.parse(
          "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$_geminiApiKey",
        );

        final systemInstruction =
            "You are Waleey (وَلِيّ) — an Islamic AI Companion for the MoodVerses platform. Answer questions about Islam, the Holy Quran, Sunnah, Islamic history, and Seerah with authentic citations (Surah:Ayah, Sahih Hadith).\n"
            "${(userName != null && userName.trim().isNotEmpty) ? "The user's name is ${userName.trim()}. Address them respectfully by name when appropriate.\n" : ""}"
            "- Provide thorough, detailed, and completely finished explanations. NEVER truncate or stop mid-sentence.\n"
            "- Format your answers with clear markdown bullet points and bold headings. Never wrap words in doubled quotation marks; use standard bold **word** instead.";

        final contents = <Map<String, dynamic>>[];

        // Prior history with strict alternation
        final priorHistory = history
            .where((m) => !m.text.startsWith("Assalamu Alaikum wa Rahmatullahi") && m.text.trim() != prompt.trim())
            .toList();

        final recent = priorHistory.length > 6 ? priorHistory.sublist(priorHistory.length - 6) : priorHistory;

        String lastRole = "";
        for (final msg in recent) {
          final role = msg.isUser ? "user" : "model";
          if (role != lastRole) {
            contents.add({
              "role": role,
              "parts": [
                {"text": msg.text.trim()}
              ]
            });
            lastRole = role;
          }
        }

        if (lastRole == "user" && contents.isNotEmpty) {
          contents.removeLast();
        }

        // Current prompt
        contents.add({
          "role": "user",
          "parts": [
            {"text": prompt}
          ]
        });

        final body = jsonEncode({
          "system_instruction": {
            "parts": [
              {"text": systemInstruction}
            ]
          },
          "contents": contents,
          "generationConfig": {
            "temperature": 0.65,
            "maxOutputTokens": 2500,
          }
        });

        final response = await http
            .post(
              url,
              headers: {"Content-Type": "application/json"},
              body: body,
            )
            .timeout(const Duration(seconds: 35));

        debugPrint("Gemini $model response status: ${response.statusCode}");

        if (response.statusCode == 200) {
          final data = jsonDecode(utf8.decode(response.bodyBytes));
          final candidates = data['candidates'] as List?;
          if (candidates != null && candidates.isNotEmpty) {
            final content = candidates[0]['content'];
            final parts = content?['parts'] as List?;
            if (parts != null && parts.isNotEmpty) {
              final rawReply = parts[0]['text'] as String? ?? "";
              final cleanReply = rawReply
                  .replaceAll(RegExp(r'^(?:Wa\s*Alaikum\s*Assalam[^.!?,]*[.!?,]|Assalamu\s*Alaikum[^.!?,]*[.!?,])', caseSensitive: false), '')
                  .replaceAll(RegExp(r'["“”]{2,}([^"“”]+)["“”]{2,}'), '**\$1**')
                  .replaceAll(RegExp(r"'{2,}([^']+)'{2,}"), '*\$1*')
                  .trim();

              final extracted = extractAuthenticCitations(cleanReply);
              return ChatMessage(
                text: cleanReply,
                isUser: false,
                citations: extracted,
                suggestions: List.from(starterPrompts)..shuffle(),
              );
            }
          }
        }
      } catch (e) {
        debugPrint("Gemini $model call exception: $e");
      }
    }
    return null;
  }
}
