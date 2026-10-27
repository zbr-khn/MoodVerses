package com.moodverses.app.ui.components

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moodverses.app.ui.theme.*

data class TourStep(
    val title: String,
    val arabicTitle: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val tip: String
)

object OnboardingManager {
    private const val PREFS_NAME = "moodverses_onboarding_prefs"
    private const val KEY_COMPLETED = "has_seen_onboarding_v2"

    fun hasCompletedTour(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_COMPLETED, false)
    }

    fun markTourCompleted(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply()
    }
}

@Composable
fun OnboardingTourDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentStepIndex by remember { mutableIntStateOf(0) }

    val steps = listOf(
        TourStep(
            title = "Mood-Based Quran Exploration",
            arabicTitle = "الاستكشاف حسب المشاعر",
            description = "Discover authentic Quranic verses curated for 12 spiritual emotional states — including Anxiety, Sadness, Gratitude, Loneliness, Seeking Guidance, and Heartbreak.",
            icon = Icons.Default.Explore,
            iconColor = EmeraldPrimary,
            tip = "💡 Tap any mood card on the Explore tab to dive into soothing Ayat for that feeling."
        ),
        TourStep(
            title = "Contemplative Ayah Reader",
            arabicTitle = "تلاوة وتدبر الآيات",
            description = "Immerse yourself in authentic Uthmanic Arabic calligraphy, accurate English transliterations, verified translations, and spiritual reflections.",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            iconColor = GoldLight,
            tip = "💡 Tap the Heart icon on any verse to save it into your favorites collection."
        ),
        TourStep(
            title = "Waleey AI Companion",
            arabicTitle = "وَلِيّ — رفيقك الإسلامي الذكي",
            description = "Ask questions about the Holy Quran, Prophetic Seerah (Ta'if, Badr, Hijrah), Islamic Golden Age scholars, and Salah guidance. Waleey provides verified citations.",
            icon = Icons.Default.AutoAwesome,
            iconColor = GoldAccent,
            tip = "💡 Every citation is interactive — tap any reference to explore its full context and commentary."
        ),
        TourStep(
            title = "Digital Tasbih Counter",
            arabicTitle = "المسبحة الإلكترونية والأذكار",
            description = "Recite daily Dhikr (SubhanAllah, Alhamdulillah, Allahu Akbar, Astaghfirullah) with physical haptic vibration and tap feedback on every bead.",
            icon = Icons.Default.TouchApp,
            iconColor = EmeraldPrimary,
            tip = "💡 Complete 33 or 100 beads to celebrate cycle completions with vibration cues."
        ),
        TourStep(
            title = "Cross-Device Cloud Sync",
            arabicTitle = "المزامنة السحابية والمحفوظات",
            description = "Log in with your Mobile Phone OTP, Email, or Google account. Your liked Ayat and Waleey AI conversations will back up and sync across all your devices automatically!",
            icon = Icons.Default.CloudDone,
            iconColor = GoldAccent,
            tip = "💡 You can always use the app offline as a guest — cloud sync activates whenever you log in."
        )
    )

    val currentStep = steps[currentStepIndex]
    val isLastStep = currentStepIndex == steps.lastIndex

    Dialog(
        onDismissRequest = {
            OnboardingManager.markTourCompleted(context)
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = EmeraldDeep,
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Skip button & Step indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldSurfaceVariant,
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                    ) {
                        Text(
                            text = "Step ${currentStepIndex + 1} of ${steps.size}",
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    TextButton(
                        onClick = {
                            OnboardingManager.markTourCompleted(context)
                            onDismiss()
                        }
                    ) {
                        Text("Skip Tour", color = TextMuted, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Icon Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(EmeraldSurface)
                        .border(2.dp, currentStep.iconColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentStep.icon,
                        contentDescription = null,
                        tint = currentStep.iconColor,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Arabic & English Title
                Text(
                    text = currentStep.arabicTitle,
                    color = GoldAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentStep.title,
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = currentStep.description,
                    color = TextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tip Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EmeraldSurfaceVariant,
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentStep.tip,
                        color = GoldLight,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.indices.forEach { index ->
                        val isSelected = index == currentStepIndex
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isSelected) GoldAccent else TextMuted.copy(alpha = 0.4f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStepIndex > 0) {
                        OutlinedButton(
                            onClick = { currentStepIndex-- },
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back", color = TextSecondary)
                        }
                    }

                    Button(
                        onClick = {
                            if (isLastStep) {
                                OnboardingManager.markTourCompleted(context)
                                onDismiss()
                            } else {
                                currentStepIndex++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(if (currentStepIndex > 0) 1.5f else 1f)
                            .height(46.dp)
                    ) {
                        Text(
                            text = if (isLastStep) "Get Started 🚀" else "Next",
                            color = EmeraldDeep,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (!isLastStep) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = EmeraldDeep, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
