package com.moodverses.app.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodverses.app.ui.theme.*

data class DhikrItem(
    val name: String,
    val arabic: String,
    val translation: String,
    val target: Int = 33
)

@Composable
fun TasbihScreen() {
    val context = LocalContext.current
    var count by remember { mutableStateOf(0) }

    val presets = remember {
        listOf(
            DhikrItem("SubhanAllah", "سُبْحَانَ اللَّهِ", "Glory be to Allah", 33),
            DhikrItem("Alhamdulillah", "الْحَمْدُ لِلَّهِ", "All praise is to Allah", 33),
            DhikrItem("Allahu Akbar", "اللَّهُ أَكْبَرُ", "Allah is the Greatest", 33),
            DhikrItem("Astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", "I seek Allah's forgiveness", 100),
            DhikrItem("La ilaha illallah", "لَا إِلَٰهَ إِلَّا اللَّهُ", "There is no god but Allah", 100)
        )
    }

    var selectedDhikr by remember { mutableStateOf(presets.first()) }

    fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDeep)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Digital Tasbih (المسبحة)",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Preset Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets) { item ->
                    val isSelected = item == selectedDhikr
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDhikr = item },
                        label = { Text(item.name, color = if (isSelected) EmeraldDeep else GoldAccent) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            containerColor = EmeraldSurface
                        )
                    )
                }
            }

            Text(
                text = selectedDhikr.translation,
                color = TextSecondary,
                fontSize = 14.sp
            )
        }

        // Tap Orb
        Surface(
            shape = CircleShape,
            color = EmeraldSurface,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(4.dp, GoldAccent),
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .clickable {
                    count++
                    vibrate()
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = selectedDhikr.arabic,
                    color = GoldAccent,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$count",
                    color = Color.White,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "TAP TO COUNT",
                    color = TextMuted,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
            }
        }

        // Reset Button
        Button(
            onClick = { count = 0 },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(bottom = 60.dp)
        ) {
            Text("Reset to 0", color = Color.White)
        }
    }
}
