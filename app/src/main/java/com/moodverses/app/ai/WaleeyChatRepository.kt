package com.moodverses.app.ai

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class ChatSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "New Conversation",
    val messages: List<ChatMessage> = listOf(WaleeyAiAgent.getInitialGreeting()),
    val updatedAt: Long = System.currentTimeMillis()
)

class WaleeyChatRepository(private val context: Context) {

    private val storageFile: File
        get() = File(context.filesDir, "waleey_chat_history.json")

    @Synchronized
    fun loadSessions(): List<ChatSession> {
        return try {
            if (!storageFile.exists()) {
                val initial = ChatSession()
                saveSessions(listOf(initial))
                return listOf(initial)
            }

            val jsonString = storageFile.readText(Charsets.UTF_8)
            val jsonArray = JSONArray(jsonString)
            val sessions = mutableListOf<ChatSession>()

            for (i in 0 until jsonArray.length()) {
                val sessionObj = jsonArray.getJSONObject(i)
                val id = sessionObj.optString("id")
                val title = sessionObj.optString("title", "Conversation")
                val updatedAt = sessionObj.optLong("updatedAt", System.currentTimeMillis())

                val msgArray = sessionObj.optJSONArray("messages") ?: JSONArray()
                val messages = mutableListOf<ChatMessage>()
                for (j in 0 until msgArray.length()) {
                    val mObj = msgArray.getJSONObject(j)
                    val mId = mObj.optString("id")
                    val text = mObj.optString("text")
                    val isUser = mObj.optBoolean("isUser")
                    val ts = mObj.optLong("timestamp")

                    val citArray = mObj.optJSONArray("citations") ?: JSONArray()
                    val citations = (0 until citArray.length()).map { citArray.getString(it) }

                    val sugArray = mObj.optJSONArray("suggestions") ?: JSONArray()
                    val suggestions = (0 until sugArray.length()).map { sugArray.getString(it) }

                    messages.add(
                        ChatMessage(
                            id = mId,
                            text = text,
                            isUser = isUser,
                            timestamp = ts,
                            citations = citations,
                            suggestions = suggestions
                        )
                    )
                }

                sessions.add(
                    ChatSession(
                        id = id,
                        title = title,
                        messages = if (messages.isEmpty()) listOf(WaleeyAiAgent.getInitialGreeting()) else messages,
                        updatedAt = updatedAt
                    )
                )
            }

            if (sessions.isEmpty()) {
                val initial = ChatSession()
                saveSessions(listOf(initial))
                listOf(initial)
            } else {
                sessions.sortedByDescending { it.updatedAt }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val fallback = listOf(ChatSession())
            fallback
        }
    }

    @Synchronized
    fun saveSessions(sessions: List<ChatSession>) {
        try {
            val jsonArray = JSONArray()
            for (session in sessions) {
                val sObj = JSONObject().apply {
                    put("id", session.id)
                    put("title", session.title)
                    put("updatedAt", session.updatedAt)

                    val msgArray = JSONArray()
                    for (msg in session.messages) {
                        val mObj = JSONObject().apply {
                            put("id", msg.id)
                            put("text", msg.text)
                            put("isUser", msg.isUser)
                            put("timestamp", msg.timestamp)
                            put("citations", JSONArray(msg.citations))
                            put("suggestions", JSONArray(msg.suggestions))
                        }
                        msgArray.put(mObj)
                    }
                    put("messages", msgArray)
                }
                jsonArray.put(sObj)
            }
            storageFile.writeText(jsonArray.toString(), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveSingleSession(session: ChatSession) {
        val current = loadSessions().toMutableList()
        val index = current.indexOfFirst { it.id == session.id }
        if (index >= 0) {
            current[index] = session
        } else {
            current.add(0, session)
        }
        saveSessions(current)
    }

    fun deleteSession(sessionId: String): List<ChatSession> {
        val current = loadSessions().toMutableList()
        current.removeAll { it.id == sessionId }
        if (current.isEmpty()) {
            current.add(ChatSession())
        }
        saveSessions(current)
        return current
    }

    fun generateTitle(firstUserQuery: String): String {
        val clean = firstUserQuery.trim()
            .replace(Regex("[\"']"), "")
            .take(35)
        return if (clean.length < firstUserQuery.trim().length) "$clean..." else clean
    }
}
