package com.example.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val currentSessionId by viewModel.currentSessionId.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Active session details
    val activeSession = remember(sessions, currentSessionId) {
        sessions.find { it.id == currentSessionId }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = GalaxyDark.copy(alpha = 0.95f),
                drawerContentColor = Color.White,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Drawer Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp, top = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(NeonCyan, NeonMagenta)),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "GEEKSY LABS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Subspace Deck v1.0",
                                fontSize = 12.sp,
                                color = StellarMutedGray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // "New Chat" Cosmic Button
                    Button(
                        onClick = {
                            viewModel.createNewSession("Cosmic Link ${sessions.size + 1}")
                            coroutineScope.launch { drawerState.close() }
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.2f), CosmicPurple.copy(alpha = 0.2f))),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Launch New Link",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = "CONVO LIST",
                        fontSize = 11.sp,
                        color = StellarMutedGray,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    // Historic list of sessions
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sessions) { session ->
                            val isSelected = session.id == currentSessionId
                            val bgBrush = if (isSelected) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        GalaxyMidnight.copy(alpha = 0.9f),
                                        GalaxyDeepPurple.copy(alpha = 0.7f)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(Color.Transparent, Color.Transparent)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(bgBrush, shape = RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.selectSession(session.id)
                                        coroutineScope.launch { drawerState.close() }
                                        focusManager.clearFocus()
                                    }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonCyan else StellarMutedGray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = session.title,
                                    color = if (isSelected) Color.White else StellarMutedGray,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = FontFamily.SansSerif,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Thread",
                                        tint = NeonMagenta.copy(alpha = 0.8f),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                viewModel.deleteSession(session)
                                            }
                                    )
                                }
                            }
                        }
                    }

                    // Footer Profile Section
                    Divider(color = GalaxyMidnight, modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(GalaxyMidnight, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "U",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Interstellar User",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(HyperSpaceGreen, shape = CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Warp Drive: ACTIVE",
                                    color = HyperSpaceGreen,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        // Main view content
        Box(modifier = Modifier.fillMaxSize()) {
            // Immutable Twinkling Space Background
            TwinklingStarsBackground()

            Scaffold(
                containerColor = Color.Transparent, // Let the starry background shine through!
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(HyperSpaceGreen, shape = CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GEEKSY AI",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 2.sp
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Terminal Menu",
                                    tint = NeonCyan
                                )
                            }
                        },
                        actions = {
                            if (activeSession != null) {
                                IconButton(onClick = { viewModel.deleteSession(activeSession) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Convo",
                                        tint = NeonMagenta.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = GalaxyDark.copy(alpha = 0.5f),
                            titleContentColor = Color.White
                        )
                    )
                },
                modifier = modifier.fillMaxSize()
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    val lazyListState = rememberLazyListState()

                    // Automatically scroll to the latest message when it arrives
                    LaunchedEffect(messages.size) {
                        if (messages.isNotEmpty()) {
                            lazyListState.animateScrollToItem(messages.size - 1)
                        }
                    }

                    // Main chat flow
                    if (messages.isEmpty()) {
                        // BREATHTAKING GALAXY CHAT EMPTY STATE
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CosmicOrb(isThinking = false)
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "GEEKSY",
                                color = Color.White,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 6.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            
                            Text(
                                text = "The Grok-Witty Galactic AI",
                                color = NeonCyan,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 32.dp)
                            )

                            // Interactive Starter Suggestions
                            Text(
                                text = "CHOOSE INITIAL VECTOR:",
                                fontSize = 11.sp,
                                color = StellarMutedGray,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            val prompts = listOf(
                                "🚀 Core Lore: Explain dark matter in 2 sassy sentences." to "Explain dark matter in 2 sassy sentences.",
                                "💻 Warp Code: Help me write a clean Kotlin coroutine call." to "Write a clean Kotlin coroutine call with error handling.",
                                "🪐 Quantum Roast: What's the biggest flaw of humanity?" to "What's the biggest flaw of humanity? Give a snappy, Grok-style roast."
                            )

                            prompts.forEach { (display, promptToSend) ->
                                FuturisticNeonCard(
                                    borderColors = listOf(NeonCyan.copy(alpha = 0.4f), CosmicPurple.copy(alpha = 0.4f)),
                                    borderWidth = 1.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            viewModel.sendMessage(promptToSend)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = display,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // ACTIVE CHAT STREAM
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                        ) {
                            items(messages) { message ->
                                val isUser = message.role == "user"
                                val bubbleShape = if (isUser) {
                                    RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
                                } else {
                                    RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                ) {
                                    if (isUser) {
                                        // USER BUBBLE
                                        Box(
                                            modifier = Modifier
                                                .widthIn(max = 290.dp)
                                                .background(
                                                    brush = Brush.linearGradient(listOf(CosmicPurple.copy(alpha = 0.3f), GalaxyMidnight.copy(alpha = 0.8f))),
                                                    shape = bubbleShape
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = CosmicPurple.copy(alpha = 0.5f),
                                                    shape = bubbleShape
                                                )
                                                .padding(14.dp)
                                        ) {
                                            Text(
                                                text = message.content,
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    } else {
                                        // BOT BUBBLE (GEEKSY)
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(
                                                            if (message.isPending) NeonCyan else HyperSpaceGreen,
                                                            shape = CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Geeksy",
                                                    fontSize = 11.sp,
                                                    color = StellarMutedGray,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            
                                            FuturisticNeonCard(
                                                borderColors = if (message.isError) {
                                                    listOf(NeonMagenta, Color.Red)
                                                } else if (message.isPending) {
                                                    listOf(NeonCyan, CosmicPurple)
                                                } else {
                                                    listOf(NeonCyan.copy(alpha = 0.7f), CosmicPurple.copy(alpha = 0.7f))
                                                },
                                                borderWidth = 1.dp,
                                                modifier = Modifier.widthIn(max = 300.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(14.dp)) {
                                                    if (message.isPending) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            TypingIndicator()
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(
                                                                text = "Receiving subspace beam...",
                                                                fontSize = 12.sp,
                                                                color = StellarMutedGray,
                                                                fontFamily = FontFamily.Monospace
                                                            )
                                                        }
                                                    } else {
                                                        MarkdownOrTextBubble(text = message.content)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // INPUT CONSOLE DECK
                    var inputText by remember { mutableStateOf("") }
                    val isPending = remember(messages) { messages.lastOrNull()?.isPending == true }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (isPending) "Geeksy is thinking..." else "Request interstellar intel...",
                                    color = StellarMutedGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            },
                            enabled = !isPending,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.4f), CosmicPurple.copy(alpha = 0.4f))),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GalaxyDark.copy(alpha = 0.8f),
                                unfocusedContainerColor = GalaxyDark.copy(alpha = 0.6f),
                                disabledContainerColor = GalaxyDark.copy(alpha = 0.4f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = StellarMutedGray,
                                cursorColor = NeonCyan,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank() && !isPending) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                    keyboardController?.hide()
                                }
                            },
                            enabled = inputText.isNotBlank() && !isPending,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = if (inputText.isNotBlank() && !isPending) {
                                        Brush.linearGradient(listOf(NeonCyan, CosmicPurple))
                                    } else {
                                        Brush.linearGradient(listOf(GalaxyMidnight, GalaxyMidnight))
                                    },
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Transmission",
                                tint = if (inputText.isNotBlank() && !isPending) Color.White else StellarMutedGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


