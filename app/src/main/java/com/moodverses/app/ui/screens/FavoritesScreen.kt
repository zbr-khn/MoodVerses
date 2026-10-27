package com.moodverses.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodverses.app.model.QuranVerse
import com.moodverses.app.ui.theme.*

@Composable
fun FavoritesScreen(
    favorites: List<QuranVerse>,
    onSelectAyah: (QuranVerse) -> Unit,
    onRemoveFavorite: (QuranVerse) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeep)
            .padding(16.dp)
    ) {
        Text(
            text = "Saved Favorites (المحفوظات)",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No saved Ayat yet.", color = Color.White, fontSize = 16.sp)
                    Text(
                        "Tap the heart icon on any Ayah to collect it here.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites) { verse ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
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
                                        text = verse.surahName.uppercase(),
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
                                        tint = Color.Gray
                                    )
                                }
                            }
                            Text(
                                text = verse.arabicText,
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 26.sp
                            )
                            Text(
                                text = "\"${verse.translation}\"",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic
                            )
                            Text(
                                text = "— ${verse.reference}",
                                color = TextGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}
