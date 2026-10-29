import 'package:uuid/uuid.dart';

class ChatMessage {
  final String id;
  final String text;
  final bool isUser;
  final int timestamp;
  final List<String> citations;
  final List<String> suggestions;

  ChatMessage({
    String? id,
    required this.text,
    required this.isUser,
    int? timestamp,
    List<String>? citations,
    List<String>? suggestions,
  })  : id = id ?? const Uuid().v4(),
        timestamp = timestamp ?? DateTime.now().millisecondsSinceEpoch,
        citations = citations ?? const [],
        suggestions = suggestions ?? const [];

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'text': text,
      'isUser': isUser,
      'timestamp': timestamp,
      'citations': citations,
      'suggestions': suggestions,
    };
  }

  factory ChatMessage.fromMap(Map<String, dynamic> map) {
    return ChatMessage(
      id: map['id'] ?? const Uuid().v4(),
      text: map['text'] ?? '',
      isUser: map['isUser'] ?? false,
      timestamp: map['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      citations: List<String>.from(map['citations'] ?? []),
      suggestions: List<String>.from(map['suggestions'] ?? []),
    );
  }
}

class ChatSession {
  final String id;
  final String title;
  final List<ChatMessage> messages;
  final int updatedAt;

  ChatSession({
    String? id,
    this.title = "New Conversation",
    List<ChatMessage>? messages,
    int? updatedAt,
  })  : id = id ?? const Uuid().v4(),
        messages = messages ?? [getInitialGreeting()],
        updatedAt = updatedAt ?? DateTime.now().millisecondsSinceEpoch;

  static ChatMessage getInitialGreeting() {
    return ChatMessage(
      text:
          "Assalamu Alaikum wa Rahmatullahi wa Barakatuh! 🌿 I am **Waleey** (وَلِيّ), your Islamic AI companion.\n\n"
          "You can ask me questions about the **Holy Quran**, authentic **Prophetic Seerah**, **Hadith**, Islamic history, and daily worship. Every response includes verified references.\n\n"
          "How may I assist your reflection today?",
      isUser: false,
      citations: const ["Surah Al-Baqarah 2:257", "Sahih Muslim 2699"],
      suggestions: const [
        "Prophet's ﷺ Mercy at Ta'if",
        "How the Quran was compiled",
        "Attaining Khushu in Salah",
        "Virtues of Tahajjud",
      ],
    );
  }

  ChatSession copyWith({
    String? id,
    String? title,
    List<ChatMessage>? messages,
    int? updatedAt,
  }) {
    return ChatSession(
      id: id ?? this.id,
      title: title ?? this.title,
      messages: messages ?? this.messages,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'updatedAt': updatedAt,
      'messages': messages.map((m) => m.toMap()).toList(),
    };
  }

  factory ChatSession.fromMap(Map<String, dynamic> map) {
    final rawMessages = map['messages'] as List? ?? [];
    return ChatSession(
      id: map['id'] ?? const Uuid().v4(),
      title: map['title'] ?? 'Conversation',
      updatedAt: map['updatedAt'] ?? DateTime.now().millisecondsSinceEpoch,
      messages: rawMessages.isNotEmpty
          ? rawMessages.map((m) => ChatMessage.fromMap(Map<String, dynamic>.from(m))).toList()
          : [getInitialGreeting()],
    );
  }
}
