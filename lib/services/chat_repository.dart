import 'dart:convert';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/chat_message.dart';

class ChatRepository {
  static const String _storageKey = 'waleey_chat_history_v2';

  static FirebaseFirestore get _firestore => FirebaseFirestore.instance;

  static Future<List<ChatSession>> loadSessions() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final jsonStr = prefs.getString(_storageKey);
      if (jsonStr == null || jsonStr.isEmpty) {
        final initial = [ChatSession()];
        await saveSessions(initial);
        return initial;
      }
      final List<dynamic> list = jsonDecode(jsonStr);
      final sessions = list.map((item) => ChatSession.fromMap(Map<String, dynamic>.from(item))).toList();
      return sessions.isNotEmpty ? sessions : [ChatSession()];
    } catch (e) {
      return [ChatSession()];
    }
  }

  static Future<void> saveSessions(List<ChatSession> sessions) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final jsonStr = jsonEncode(sessions.map((s) => s.toMap()).toList());
      await prefs.setString(_storageKey, jsonStr);
    } catch (e) {
      // Ignore
    }
  }

  static Future<void> saveSingleSession(ChatSession session) async {
    final current = await loadSessions();
    final index = current.indexWhere((s) => s.id == session.id);
    if (index >= 0) {
      current[index] = session;
    } else {
      current.insert(0, session);
    }
    await saveSessions(current);
  }

  static Future<List<ChatSession>> deleteSession(String sessionId) async {
    final current = await loadSessions();
    current.removeWhere((s) => s.id == sessionId);
    final finalSessions = current.isNotEmpty ? current : [ChatSession()];
    await saveSessions(finalSessions);
    return finalSessions;
  }

  // Cloud Firestore Sync
  static Future<void> syncSessionToCloud(String userId, ChatSession session) async {
    if (userId.isEmpty) return;
    try {
      await _firestore
          .collection('users')
          .doc(userId)
          .collection('chat_sessions')
          .doc(session.id)
          .set(session.toMap(), SetOptions(merge: true));
    } catch (e) {
      // Offline fallback
    }
  }

  static Future<void> deleteCloudSession(String userId, String sessionId) async {
    if (userId.isEmpty || sessionId.isEmpty) return;
    try {
      await _firestore
          .collection('users')
          .doc(userId)
          .collection('chat_sessions')
          .doc(sessionId)
          .delete();
    } catch (e) {
      // Offline fallback
    }
  }

  static Future<List<ChatSession>> fetchCloudSessions(String userId) async {
    if (userId.isEmpty) return [];
    try {
      final snapshot = await _firestore
          .collection('users')
          .doc(userId)
          .collection('chat_sessions')
          .get();
      return snapshot.docs
          .map((doc) => ChatSession.fromMap(doc.data()))
          .toList()
        ..sort((a, b) => b.updatedAt.compareTo(a.updatedAt));
    } catch (e) {
      return [];
    }
  }
}
