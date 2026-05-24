package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.data.local.ChatDatabase
import com.example.data.repository.ChatRepository
import com.example.ui.chat.ChatScreen
import com.example.ui.chat.ChatViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize local database, DAO, and repository
    val database = ChatDatabase.getDatabase(applicationContext)
    val chatDao = database.chatDao()
    val repository = ChatRepository(chatDao)

    // Instantiate ChatViewModel using custom Factory
    val chatViewModel: ChatViewModel by viewModels {
      ChatViewModel.provideFactory(repository)
    }

    setContent {
      MyApplicationTheme {
        ChatScreen(
          viewModel = chatViewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
