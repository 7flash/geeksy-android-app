package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.ChatDao
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.remote.Content as RemoteContent
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.Part as RemotePart
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepository(private val chatDao: ChatDao) {

    val allSessions: Flow<List<ChatSession>> = chatDao.getAllSessions()

    fun getMessages(sessionId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForSession(sessionId)
    }

    suspend fun createSession(title: String): Long = withContext(Dispatchers.IO) {
        chatDao.insertSession(ChatSession(title = title))
    }

    suspend fun updateSessionTitle(sessionId: Long, title: String) = withContext(Dispatchers.IO) {
        chatDao.updateSessionTitle(sessionId, title)
    }

    suspend fun deleteSession(session: ChatSession) = withContext(Dispatchers.IO) {
        chatDao.deleteSession(session)
    }

    suspend fun sendMessage(
        sessionId: Long,
        messageText: String,
        history: List<ChatMessage>
    ) = withContext(Dispatchers.IO) {
        // 1. Save User Message
        val userMsgId = chatDao.insertMessage(
            ChatMessage(
                sessionId = sessionId,
                role = "user",
                content = messageText,
                isPending = false
            )
        )

        // 2. Save a Pending Bot Message placeholder
        val botMsgId = chatDao.insertMessage(
            ChatMessage(
                sessionId = sessionId,
                role = "model",
                content = "",
                isPending = true
            )
        )

        // 3. Prepare Chat History for Gemini API
        // Filter out pending messages and error states, only include sent ones
        val chatHistory = history.filter { !it.isPending && !it.isError } +
                ChatMessage(id = userMsgId, sessionId = sessionId, role = "user", content = messageText)

        val contents = chatHistory.map { msg ->
            RemoteContent(
                role = if (msg.role == "user") "user" else "model",
                parts = listOf(RemotePart(text = msg.content))
            )
        }

        // 4. Validate API key
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            chatDao.insertMessage(
                ChatMessage(
                    id = botMsgId,
                    sessionId = sessionId,
                    role = "model",
                    content = "API Key is missing!\n\nPlease add your `GEMINI_API_KEY` in the Secrets panel in Google AI Studio to chat with Geeksy.\n\n(Check the instruction block in AI Studio: Settings -> Secrets)",
                    isPending = false,
                    isError = true
                )
            )
            return@withContext
        }

        // 5. Invoke Gemini API
        try {
            // Provide Geeksy system instructions to give it an elite galactic hacker/expert persona
            val systemInstruction = RemoteContent(
                parts = listOf(
                    RemotePart(
                        text = "You are Geeksy, an elite, witty, futuristic conversational AI chatbot with a deep love for cosmic mysteries, galaxy lore, coding, hacking, and science fiction. " +
                               "Your tone is intellectual yet cool, snappy, and mildly sassy (inspired by Grok). " +
                               "You write neat markdown responses, and love inserting subtle galactic, interstellar, or spaceship references in your chats (e.g. 'broadcasting across the cosmos: ', 'Hold onto your space suits...'). " +
                               "Always maintain this highly intelligent, galaxy-traveler persona."
                    )
                )
            )

            val request = GenerateContentRequest(
                contents = contents,
                systemInstruction = systemInstruction
            )

            val response = RetrofitClient.service.generateContent(apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!responseText.isNullOrBlank()) {
                // Update placeholder message with real text
                chatDao.insertMessage(
                    ChatMessage(
                        id = botMsgId,
                        sessionId = sessionId,
                        role = "model",
                        content = responseText,
                        isPending = false
                    )
                )

                // Optional: If this is the first real message exchange, automatically update session title
                if (history.isEmpty()) {
                    val automaticTitle = if (messageText.length > 25) {
                        messageText.take(22) + "..."
                    } else {
                        messageText
                    }
                    chatDao.updateSessionTitle(sessionId, automaticTitle)
                }
            } else {
                chatDao.insertMessage(
                    ChatMessage(
                        id = botMsgId,
                        sessionId = sessionId,
                        role = "model",
                        content = "Received an empty cosmic transmission from the Gemini core. Please try again.",
                        isPending = false,
                        isError = true
                    )
                )
            }
        } catch (e: Exception) {
            chatDao.insertMessage(
                ChatMessage(
                    id = botMsgId,
                    sessionId = sessionId,
                    role = "model",
                    content = "Cosmic distortion error: ${e.localizedMessage ?: "Failed to receive stellar feed"}.\n\nPlease check your internet connection or try again.",
                    isPending = false,
                    isError = true
                )
            )
        }
    }
}
