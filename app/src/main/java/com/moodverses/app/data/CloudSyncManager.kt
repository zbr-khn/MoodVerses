package com.moodverses.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.moodverses.app.ai.ChatMessage
import com.moodverses.app.ai.ChatSession

object CloudSyncManager {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    /**
     * Upload or update a chat session in Cloud Firestore
     */
    fun syncSessionToCloud(userId: String, session: ChatSession) {
        if (userId.isBlank()) return

        try {
            val sessionData = hashMapOf(
                "id" to session.id,
                "title" to session.title,
                "updatedAt" to session.updatedAt,
                "messages" to session.messages.map { msg ->
                    hashMapOf(
                        "id" to msg.id,
                        "text" to msg.text,
                        "isUser" to msg.isUser,
                        "timestamp" to msg.timestamp,
                        "citations" to msg.citations,
                        "suggestions" to msg.suggestions
                    )
                }
            )

            firestore.collection("users")
                .document(userId)
                .collection("chat_sessions")
                .document(session.id)
                .set(sessionData, SetOptions.merge())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Fetch all chat sessions for the current user from Cloud Firestore
     */
    fun fetchUserSessions(
        userId: String,
        onComplete: (List<ChatSession>) -> Unit
    ) {
        if (userId.isBlank()) {
            onComplete(emptyList())
            return
        }

        try {
            firestore.collection("users")
                .document(userId)
                .collection("chat_sessions")
                .get()
                .addOnSuccessListener { snapshot ->
                    val sessions = mutableListOf<ChatSession>()
                    for (doc in snapshot.documents) {
                        val id = doc.getString("id") ?: doc.id
                        val title = doc.getString("title") ?: "Conversation"
                        val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()

                        val rawMessages = doc.get("messages") as? List<Map<String, Any>> ?: emptyList()
                        val messages = rawMessages.map { map ->
                            ChatMessage(
                                id = map["id"] as? String ?: java.util.UUID.randomUUID().toString(),
                                text = map["text"] as? String ?: "",
                                isUser = map["isUser"] as? Boolean ?: false,
                                timestamp = (map["timestamp"] as? Long) ?: System.currentTimeMillis(),
                                citations = (map["citations"] as? List<String>) ?: emptyList(),
                                suggestions = (map["suggestions"] as? List<String>) ?: emptyList()
                            )
                        }

                        sessions.add(
                            ChatSession(
                                id = id,
                                title = title,
                                messages = if (messages.isEmpty()) listOf(com.moodverses.app.ai.WaleeyAiAgent.getInitialGreeting()) else messages,
                                updatedAt = updatedAt
                            )
                        )
                    }
                    onComplete(sessions.sortedByDescending { it.updatedAt })
                }
                .addOnFailureListener {
                    onComplete(emptyList())
                }
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(emptyList())
        }
    }

    /**
     * Delete a session from Cloud Firestore
     */
    fun deleteCloudSession(userId: String, sessionId: String) {
        if (userId.isBlank() || sessionId.isBlank()) return
        try {
            firestore.collection("users")
                .document(userId)
                .collection("chat_sessions")
                .document(sessionId)
                .delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
