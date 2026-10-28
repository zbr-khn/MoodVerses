package com.moodverses.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodverses.app.R
import com.moodverses.app.ai.ChatMessage
import com.moodverses.app.ai.ChatSession
import com.moodverses.app.ai.WaleeyAiAgent
import com.moodverses.app.ai.WaleeyChatRepository
import com.moodverses.app.auth.AuthDialog
import com.moodverses.app.auth.AuthManager
import com.moodverses.app.data.CloudSyncManager
import com.moodverses.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun WaleeyScreen() {
    val context = LocalContext.current
    val chatRepo = remember { WaleeyChatRepository(context) }
    var sessions by remember { mutableStateOf(chatRepo.loadSessions()) }
    var activeSessionId by remember { mutableStateOf(sessions.firstOrNull()?.id ?: "") }

    val activeSession = remember(activeSessionId, sessions) {
        sessions.find { it.id == activeSessionId } ?: sessions.firstOrNull() ?: ChatSession().also {
            sessions = listOf(it)
            activeSessionId = it.id
        }
    }

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(AuthManager.isLoggedIn) }
    var userName by remember { mutableStateOf(AuthManager.getCustomUserName(context)) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    fun createNewChat() {
        val newSession = ChatSession()
        chatRepo.saveSingleSession(newSession)
        sessions = chatRepo.loadSessions()
        activeSessionId = newSession.id
        inputText = ""
        coroutineScope.launch {
            drawerState.close()
        }
    }

    fun sendMessage(text: String) {
        val query = WaleeyAiAgent.sanitizeInput(text)
        if (query.isEmpty() || isThinking) return

        val userMsg = ChatMessage(text = query, isUser = true)
        val updatedMessages = activeSession.messages + userMsg

        // Generate descriptive title from first question
        val newTitle = if (activeSession.title == "New Conversation") {
            chatRepo.generateTitle(query)
        } else {
            activeSession.title
        }

        val intermediateSession = activeSession.copy(
            title = newTitle,
            messages = updatedMessages,
            updatedAt = System.currentTimeMillis()
        )
        chatRepo.saveSingleSession(intermediateSession)
        sessions = chatRepo.loadSessions()

        inputText = ""
        isThinking = true

        coroutineScope.launch {
            listState.animateScrollToItem(updatedMessages.size - 1)
            // Multi-turn contextual query with personalized greeting
            val aiResponse = WaleeyAiAgent.processQuery(query, history = updatedMessages, userName = userName)
            val finalMessages = updatedMessages + aiResponse
            val finalSession = intermediateSession.copy(
                messages = finalMessages,
                updatedAt = System.currentTimeMillis()
            )
            chatRepo.saveSingleSession(finalSession)
            sessions = chatRepo.loadSessions()
            isThinking = false
            listState.animateScrollToItem(finalMessages.size - 1)

            // Sync to Firebase Cloud Firestore if logged in
            if (AuthManager.isLoggedIn) {
                AuthManager.currentUser?.uid?.let { uid ->
                    CloudSyncManager.syncSessionToCloud(uid, finalSession)
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = EmeraldSurface,
                drawerContentColor = TextPrimary,
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Chat History",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "سجل المحادثات",
                                    fontSize = 12.sp,
                                    color = GoldLight
                                )
                            }
                        }

                        IconButton(onClick = { coroutineScope.launch { drawerState.close() } }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    // "+ New Chat" Button
                    Button(
                        onClick = { createNewChat() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Chat", tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Conversation", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }

                    HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PREVIOUS SESSIONS",
                        fontSize = 11.sp,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Sessions List
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(sessions, key = { it.id }) { session ->
                            val isSelected = session.id == activeSessionId
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) EmeraldSurfaceVariant else Color.Transparent,
                                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activeSessionId = session.id
                                        coroutineScope.launch { drawerState.close() }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ChatBubbleOutline,
                                            contentDescription = null,
                                            tint = if (isSelected) GoldAccent else TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = session.title,
                                                color = if (isSelected) GoldLight else TextPrimary,
                                                fontSize = 13.5.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${session.messages.count { it.isUser }} questions",
                                                color = TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    if (sessions.size > 1) {
                                        IconButton(
                                            onClick = {
                                                val toDeleteId = session.id
                                                val remaining = chatRepo.deleteSession(toDeleteId)
                                                sessions = remaining
                                                if (activeSessionId == toDeleteId) {
                                                    activeSessionId = remaining.first().id
                                                }
                                                if (AuthManager.isLoggedIn) {
                                                    AuthManager.currentUser?.uid?.let { uid ->
                                                        CloudSyncManager.deleteCloudSession(uid, toDeleteId)
                                                    }
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete",
                                                tint = TextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

                    // Account & Cloud Status Section
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = EmeraldSurfaceVariant,
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (isLoggedIn) EmeraldPrimary.copy(alpha = 0.2f) else EmeraldDeep)
                                            .border(1.dp, if (isLoggedIn) EmeraldPrimary else BorderSubtle, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isLoggedIn) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                                            contentDescription = "Account",
                                            tint = if (isLoggedIn) EmeraldPrimary else GoldLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isLoggedIn) {
                                                AuthManager.currentUser?.displayName 
                                                    ?: AuthManager.currentUser?.phoneNumber 
                                                    ?: AuthManager.currentUser?.email 
                                                    ?: "Sync Active"
                                            } else {
                                                "Guest Account"
                                            },
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (isLoggedIn) "Cloud Sync Enabled" else "Local Storage Only",
                                            color = if (isLoggedIn) EmeraldPrimary else TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (isLoggedIn) {
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch { drawerState.close() }
                                            showLogoutConfirmation = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = "Sign Out",
                                            tint = TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            if (!isLoggedIn) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        coroutineScope.launch { drawerState.close() }
                                        showAuthDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "Sign In to Cloud Sync",
                                        color = EmeraldDeep,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EmeraldDeep)
                    .padding(16.dp)
            ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { coroutineScope.launch { drawerState.open() } }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "History Menu",
                            tint = GoldAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldSurfaceVariant)
                            .border(1.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Waleey AI",
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Waleey",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(وَلِيّ)",
                                fontSize = 14.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = if (!userName.isNullOrBlank()) "Assalamu Alaikum, $userName" else "Your Islamic AI Companion",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showAccountDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "User Profile",
                            tint = if (isLoggedIn) EmeraldPrimary else GoldLight
                        )
                    }
                    IconButton(
                        onClick = { createNewChat() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "New Chat",
                            tint = GoldLight
                        )
                    }
                }
            }

            // Horizontal Quick Prompts
            Text(
                text = "Suggested Inquiries:",
                fontSize = 11.sp,
                color = GoldLight,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                items(WaleeyAiAgent.starterPrompts) { prompt ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = EmeraldSurface,
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                        modifier = Modifier.clickable { sendMessage(prompt) }
                    ) {
                        Text(
                            text = prompt,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Chat Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeSession.messages, key = { it.id }) { msg ->
                    ChatMessageItem(
                        message = msg,
                        onSuggestionClick = { suggestion ->
                            sendMessage(suggestion)
                        }
                    )
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldSurface)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = GoldAccent,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Waleey is reflecting upon Islamic knowledge...",
                                fontSize = 12.5.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(EmeraldSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { if (it.length <= 400) inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Ask about Seerah, Salah, History, Ayats...",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = GoldAccent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 3
                )

                IconButton(
                    onClick = { sendMessage(inputText) },
                    enabled = inputText.isNotBlank() && !isThinking,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isThinking) EmeraldPrimary else EmeraldSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isThinking) EmeraldDeep else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

    if (showAuthDialog) {
        AuthDialog(
            onDismiss = { showAuthDialog = false },
            onAuthSuccess = {
                isLoggedIn = true
                showAuthDialog = false
                val uid = AuthManager.currentUser?.uid
                if (!uid.isNullOrBlank()) {
                    // Upload current local sessions to cloud
                    sessions.forEach { s ->
                        CloudSyncManager.syncSessionToCloud(uid, s)
                    }
                    // Fetch all cloud sessions and merge locally
                    CloudSyncManager.fetchUserSessions(uid) { cloudSessions ->
                        if (cloudSessions.isNotEmpty()) {
                            cloudSessions.forEach { chatRepo.saveSingleSession(it) }
                            sessions = chatRepo.loadSessions()
                            if (sessions.none { it.id == activeSessionId }) {
                                activeSessionId = sessions.first().id
                            }
                        }
                    }
                }
            }
        )
    }

    if (showAccountDialog) {
        val user = AuthManager.currentUser
        var editingName by remember { mutableStateOf(userName ?: AuthManager.getCustomUserName(context) ?: "") }
        var nameSavedMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            containerColor = EmeraldSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("User Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("Your Name (Greeting for Waleey AI)", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = editingName,
                            onValueChange = { editingName = it },
                            placeholder = { Text("Enter your name (e.g. Zubair)", color = TextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val clean = editingName.trim()
                                if (clean.isNotEmpty()) {
                                    AuthManager.saveCustomUserName(context, clean)
                                    userName = clean
                                    nameSavedMessage = "Saved as \"$clean\""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save", color = EmeraldDeep, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (nameSavedMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(nameSavedMessage!!, color = EmeraldPrimary, fontSize = 11.5.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    if (isLoggedIn) {
                        val emailOrPhone = user?.email ?: user?.phoneNumber ?: "Authenticated User"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldSurfaceVariant,
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary))
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cloud Sync Active", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(emailOrPhone, color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldSurfaceVariant,
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                Text("Guest Account", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("All chats and favorites are stored locally. Sign in to sync across devices.", color = TextMuted, fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (isLoggedIn) {
                    Button(
                        onClick = {
                            showAccountDialog = false
                            showLogoutConfirmation = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Log Out", color = Color(0xFFFCA5A5), fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Button(
                        onClick = {
                            showAccountDialog = false
                            showAuthDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sign In / Create Account", color = EmeraldDeep, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("Done", color = TextMuted)
                }
            }
        )
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            containerColor = EmeraldSurface,
            title = {
                Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(
                    "Are you sure you want to log out from MoodVerses?",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        AuthManager.signOut()
                        isLoggedIn = false
                        showLogoutConfirmation = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("You are logged out.")
                        }
                        android.widget.Toast.makeText(context, "You are logged out.", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Yes, Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onSuggestionClick: (String) -> Unit
) {
    if (message.isUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                    .background(EmeraldSurfaceVariant)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = formatMarkdownText(message.text, boldColor = TextPrimary),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = EmeraldDeep,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(EmeraldSurfaceVariant)
                    .border(1.dp, GoldAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Waleey",
                    tint = GoldAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.widthIn(max = 310.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                        .background(EmeraldSurface)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = formatMarkdownText(message.text, boldColor = GoldLight, italicColor = TextSecondary),
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            lineHeight = 21.sp
                        )

                        if (message.citations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Verified References:",
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            message.citations.forEach { citation ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldSurfaceVariant.copy(alpha = 0.7f),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = citation,
                                            fontSize = 11.5.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Suggestions under the message
                if (message.suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Related Topics:",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRowHelper(
                        items = message.suggestions,
                        onItemClick = onSuggestionClick
                    )
                }
            }
        }
    }
}

@Composable
fun FlowRowHelper(
    items: List<String>,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.take(2).forEach { suggestion ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = EmeraldSurfaceVariant,
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                modifier = Modifier
                    .clickable { onItemClick(suggestion) }
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = "✦ $suggestion",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun formatMarkdownText(
    text: String,
    primaryColor: Color = TextPrimary,
    boldColor: Color = GoldLight,
    italicColor: Color = TextSecondary
): AnnotatedString {
    return remember(text) {
        // 1. Clean double quotes around words e.g. ""word"" or \"\"word\"\" -> **word**
        val cleaned = text
            .replace(Regex("[\"“”]{2,}([^\"“”]+)[\"“”]{2,}"), "**$1**")
            .replace(Regex("'{2,}([^']+)('{2,})"), "*$1*")

        buildAnnotatedString {
            // Regex to find **bold** and *italic*
            val regex = Regex("(\\x2A\\x2A(.*?)\\x2A\\x2A|\\x2A(.*?)\\x2A)")
            var lastIndex = 0

            for (match in regex.findAll(cleaned)) {
                val start = match.range.first
                val end = match.range.last + 1

                if (start > lastIndex) {
                    append(cleaned.substring(lastIndex, start))
                }

                val fullMatch = match.value
                when {
                    fullMatch.startsWith("**") && fullMatch.endsWith("**") && fullMatch.length > 4 -> {
                        val inner = fullMatch.substring(2, fullMatch.length - 2)
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = boldColor)) {
                            append(inner)
                        }
                    }
                    fullMatch.startsWith("*") && fullMatch.endsWith("*") && fullMatch.length > 2 -> {
                        val inner = fullMatch.substring(1, fullMatch.length - 1)
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = italicColor)) {
                            append(inner)
                        }
                    }
                    else -> {
                        append(fullMatch)
                    }
                }
                lastIndex = end
            }

            if (lastIndex < cleaned.length) {
                append(cleaned.substring(lastIndex))
            }
        }
    }
}
