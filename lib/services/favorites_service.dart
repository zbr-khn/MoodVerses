import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'auth_service.dart';

class FavoritesService {
  static const String _key = 'favorite_verse_ids';
  static FirebaseFirestore get _firestore => FirebaseFirestore.instance;

  static Future<Set<String>> getLocalFavorites() async {
    final prefs = await SharedPreferences.getInstance();
    return (prefs.getStringList(_key) ?? []).toSet();
  }

  static Future<bool> toggleFavorite(String verseId) async {
    final prefs = await SharedPreferences.getInstance();
    final current = (prefs.getStringList(_key) ?? []).toSet();

    final isFav = current.contains(verseId);
    if (isFav) {
      current.remove(verseId);
    } else {
      current.add(verseId);
    }
    await prefs.setStringList(_key, current.toList());

    // Sync to Cloud Firestore if logged in
    final user = AuthService.currentUser;
    if (user != null) {
      await syncToCloud(user.uid, verseId, !isFav);
    }

    return !isFav;
  }

  static Future<void> syncToCloud(String userId, String verseId, bool isFavorite) async {
    if (userId.isEmpty || verseId.isEmpty) return;
    try {
      final doc = _firestore.collection('users').doc(userId).collection('favorite_ayahs').doc(verseId);
      if (isFavorite) {
        await doc.set({
          'verseId': verseId,
          'savedAt': DateTime.now().millisecondsSinceEpoch,
        }, SetOptions(merge: true));
      } else {
        await doc.delete();
      }
    } catch (e) {
      // Offline fallback
    }
  }

  static Future<Set<String>> fetchAndMergeCloudFavorites(String userId) async {
    final local = await getLocalFavorites();
    if (userId.isEmpty) return local;

    try {
      final snapshot = await _firestore.collection('users').doc(userId).collection('favorite_ayahs').get();
      final cloudIds = snapshot.docs.map((d) => d.data()['verseId'] as String? ?? d.id).toSet();
      final merged = {...local, ...cloudIds};

      final prefs = await SharedPreferences.getInstance();
      await prefs.setStringList(_key, merged.toList());

      // Back up missing to cloud
      for (final missing in local.difference(cloudIds)) {
        await syncToCloud(userId, missing, true);
      }

      return merged;
    } catch (e) {
      return local;
    }
  }
}
