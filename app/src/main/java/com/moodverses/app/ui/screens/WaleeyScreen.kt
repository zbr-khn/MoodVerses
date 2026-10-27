package com.moodverses.app.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodverses.app.ai.ChatMessage
import com.moodverses.app.ai.WaleeyAiAgent
import com.moodverses.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun WaleeyScreen() {
    val messages = remember { mutableStateListOf<ChatMessage>(WaleeyAiAgent.getInitialGreeting()) }
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    fun sendMessage(text: String) {
        val query = WaleeyAiAgent.sanitizeInput(text)
        if (query.isEmpty() || isThinking) return

        messages.add(ChatMessage(text = query, isUser = true))
        inputText = ""
        isThinking = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            val aiResponse = WaleeyAiAgent.processQuery(query)
            messages.add(aiResponse)
            isThinking = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

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
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EmeraldSurfaceVariant)
                        .border(1.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Waleey AI",
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Waleey",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(وَلِيّ)",
                            fontSize = 15.sp,
                            color = GoldLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "Your Islamic AI Companion",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                }
            }

            IconButton(
                onClick = {
                    messages.clear()
                    messages.add(WaleeyAiAgent.getInitialGreeting())
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Chat",
                    tint = TextMuted
                )
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
            items(messages, key = { it.id }) { msg ->
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
                            Text(
                                text = "Verified References:",
                                fontSize = 11.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            message.citations.forEach { citation ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = citation,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
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
