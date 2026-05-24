package com.example.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    // Current active session ID (null if no active session selected)
    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    // All historic chat sessions
    val sessions: StateFlow<List<ChatSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Reactive list of messages for the currently selected session
    val messages: StateFlow<List<ChatMessage>> = _currentSessionId
        .flatMapLatest { sessionId ->
            if (sessionId != null) {
                repository.getMessages(sessionId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Automatically open or create a session on startup if none exists,
        // or wait for sessions to load and pick the first one.
        viewModelScope.launch {
            repository.allSessions.collect { sessionList ->
                if (_currentSessionId.value == null && sessionList.isNotEmpty()) {
                    _currentSessionId.value = sessionList.first().id
                }
            }
        }
    }

    fun selectSession(sessionId: Long) {
        _currentSessionId.value = sessionId
    }

    fun createNewSession(title: String = "Cosmic Link") {
        viewModelScope.launch {
            val newId = repository.createSession(title)
            _currentSessionId.value = newId
        }
    }

    fun deleteSession(session: ChatSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
            if (_currentSessionId.value == session.id) {
                _currentSessionId.value = null
            }
        }
    }

    fun sendMessage(text: String) {
        val sessionId = _currentSessionId.value
        if (text.isBlank()) return

        viewModelScope.launch {
            val targetSessionId = if (sessionId == null) {
                val newId = repository.createSession("Chatting...")
                _currentSessionId.value = newId
                newId
            } else {
                sessionId
            }

            // Fire and forget send message
            repository.sendMessage(targetSessionId, text.trim(), messages.value)
        }
    }

    companion object {
        fun provideFactory(repository: ChatRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ChatViewModel(repository) as T
                }
            }
    }
}
