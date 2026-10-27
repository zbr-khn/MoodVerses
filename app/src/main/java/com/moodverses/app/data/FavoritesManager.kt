package com.moodverses.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.moodverses.app.auth.AuthManager
import com.moodverses.app.model.QuranVerse

object FavoritesManager {

    private const val PREFS_NAME = "moodverses_favorites_prefs"
    private const val KEY_FAVORITE_IDS = "favorite_verse_ids"

    private var prefs: SharedPreferences? = null
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * Load all locally saved verse IDs
     */
    fun getLocalFavoriteIds(): Set<String> {
        return prefs?.getStringSet(KEY_FAVORITE_IDS, emptySet()) ?: emptySet()
    }

    /**
     * Save local verse IDs
     */
    fun saveLocalFavoriteIds(ids: Set<String>) {
        prefs?.edit()?.putStringSet(KEY_FAVORITE_IDS, ids)?.apply()
    }

    /**
     * Toggle favorite status (persisting both locally and to Cloud Firestore if logged in)
     */
    fun toggleFavorite(verseId: String): Boolean {
        val current = getLocalFavoriteIds().toMutableSet()
        val isNowFavorite: Boolean
        if (current.contains(verseId)) {
            current.remove(verseId)
            isNowFavorite = false
        } else {
            current.add(verseId)
            isNowFavorite = true
        }
        saveLocalFavoriteIds(current)

        // Sync to cloud if user is logged in
        val user = AuthManager.currentUser
        if (user != null) {
            syncFavoriteToCloud(user.uid, verseId, isNowFavorite)
        }

        return isNowFavorite
    }

    /**
     * Sync a single favorite addition or removal to Cloud Firestore
     */
    fun syncFavoriteToCloud(userId: String, verseId: String, isFavorite: Boolean) {
        if (userId.isBlank() || verseId.isBlank()) return
        try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("favorite_ayahs")
                .document(verseId)

            if (isFavorite) {
                val data = hashMapOf(
                    "verseId" to verseId,
                    "savedAt" to System.currentTimeMillis()
                )
                docRef.set(data, SetOptions.merge())
            } else {
                docRef.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Pull all favorites from Cloud Firestore and merge with local
     */
    fun fetchAndMergeCloudFavorites(userId: String, onComplete: (Set<String>) -> Unit) {
        if (userId.isBlank()) {
            onComplete(getLocalFavoriteIds())
            return
        }

        try {
            firestore.collection("users")
                .document(userId)
                .collection("favorite_ayahs")
                .get()
                .addOnSuccessListener { snapshot ->
                    val cloudIds = snapshot.documents.mapNotNull { it.getString("verseId") ?: it.id }.toSet()
                    val localIds = getLocalFavoriteIds()
                    val merged = localIds + cloudIds
                    saveLocalFavoriteIds(merged)

                    // Also upload any local favorites that were missing in the cloud
                    (localIds - cloudIds).forEach { missingId ->
                        syncFavoriteToCloud(userId, missingId, true)
                    }

                    onComplete(merged)
                }
                .addOnFailureListener {
                    onComplete(getLocalFavoriteIds())
                }
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(getLocalFavoriteIds())
        }
    }
}
