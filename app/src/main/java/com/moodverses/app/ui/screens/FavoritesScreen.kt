package com.moodverses.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodverses.app.model.QuranVerse
import com.moodverses.app.ui.theme.*

@Composable
fun FavoritesScreen(
    favorites: List<QuranVerse>,
    isLoggedIn: Boolean = false,
    userDisplay: String? = null,
    onSignInClick: () -> Unit = {},
    onSelectAyah: (QuranVerse) -> Unit,
    onRemoveFavorite: (QuranVerse) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeep)
            .padding(16.dp)
    ) {
        // Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Saved Favorites",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "المحفوظات القرآنية",
                    color = GoldLight,
                    fontSize = 12.sp
                )
            }

            Surface(
                color = EmeraldSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Text(
                    text = "${favorites.size} Ayat",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Cloud Sync Information Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isLoggedIn) EmeraldSurfaceVariant else EmeraldSurface,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(if (isLoggedIn) EmeraldPrimary else GoldAccent)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isLoggedIn) EmeraldPrimary.copy(alpha = 0.2f) else EmeraldDeep, CircleShape)
                            .border(1.dp, if (isLoggedIn) EmeraldPrimary else GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isLoggedIn) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                            contentDescription = "Cloud Status",
                            tint = if (isLoggedIn) EmeraldPrimary else GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isLoggedIn) "Cloud Backup Active" else "Cloud Sync Available",
                            color = if (isLoggedIn) EmeraldPrimary else GoldLight,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isLoggedIn) {
                                "Synced to ${userDisplay ?: "account"} • Safe on all devices"
                            } else {
                                "Log in to access your liked Ayat on other devices"
                            },
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (!isLoggedIn) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSignInClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            color = EmeraldDeep,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Favorites List or Empty State
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No saved Ayat yet.", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tap the heart icon on any Ayah in Explore to collect it here.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { it.id }) { verse ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAyah(verse) }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                                        text = "${verse.surahName.uppercase()} (${verse.surahNumber}:${verse.ayahNumber})",
                                        color = GoldAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onRemoveFavorite(verse) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = verse.arabicText,
                                color = GoldLight,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp
                            )

                            Text(
                                text = "\"${verse.translation}\"",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
