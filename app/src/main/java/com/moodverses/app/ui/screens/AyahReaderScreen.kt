package com.moodverses.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodverses.app.model.QuranVerse
import com.moodverses.app.ui.theme.*

@Composable
fun AyahReaderScreen(
    verses: List<QuranVerse>,
    currentIndex: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: (QuranVerse) -> Unit
) {
    val context = LocalContext.current
    var showReflection by remember { mutableStateOf(false) }

    if (verses.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EmeraldDeep),
            contentAlignment = Alignment.Center
        ) {
            Text("No verses available for this filter.", color = Color.White)
        }
        return
    }

    val verse = verses[currentIndex.coerceIn(0, verses.lastIndex)]
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeep)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0x33D4AF37),
                shape = CircleShape
            ) {
                Text(
                    text = verse.arabicMood,
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onPrev,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = EmeraldSurface)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = Color.White)
                }
                IconButton(
                    onClick = onNext,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = EmeraldSurface)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White)
                }
            }
        }

        // Master Ayah Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Surah Badge & Mood Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0x33D4AF37),
                        shape = CircleShape
                    ) {
                        Text(
                            text = verse.surahName.uppercase(),
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Text(
                        text = verse.moodLabel,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Arabic Script
                Text(
                    text = verse.arabicText,
                    color = Color.White,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )

                // Transliteration
                if (verse.transliteration.isNotBlank()) {
                    Text(
                        text = verse.transliteration,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)

                // English Translation
                Text(
                    text = "\"${verse.translation}\"",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "— ${verse.reference}",
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Expandable Reflection Box
                Surface(
                    color = Color(0x40000000),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReflection = !showReflection }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Why This Comforts (تدبر)",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (showReflection) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = GoldAccent
                            )
                        }
                        AnimatedVisibility(visible = showReflection) {
                            Text(
                                text = verse.reflection,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)

                // Toolbar Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { onToggleFavorite(verse) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else GoldAccent
                        )
                    }

                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Ayah", "${verse.arabicText}\n\n\"${verse.translation}\"\n— ${verse.reference}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Ayah copied to clipboard", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldAccent)
                    }
                }
            }
        }

        // Quick Bottom Next / Prev
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPrev,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("← Previous", color = Color.White)
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Next Ayah →", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
