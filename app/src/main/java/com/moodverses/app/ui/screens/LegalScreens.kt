package com.moodverses.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moodverses.app.ui.theme.*

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(EmeraldDeep),
            color = EmeraldDeep
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Privacy Policy",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GoldAccent)
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(EmeraldSurface, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = "MoodVerses Privacy Policy",
                        color = GoldLight,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Effective Date: November 2026",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    LegalSection(
                        title = "1. Offline-First Privacy",
                        content = "MoodVerses is built with an offline-first philosophy. When using the app as a guest, zero personal information is collected, tracked, or transmitted to any server. Your saved favorite Ayat, custom profile name, and Tasbih counts remain exclusively on your device."
                    )

                    LegalSection(
                        title = "2. Optional Cloud Synchronization",
                        content = "If you choose to sign in (via Google Sign-In, Email/Password, or Phone OTP), authentication is handled securely by Google Firebase Authentication. Your email or phone number is stored solely to maintain your session and sync your saved favorites across your devices."
                    )

                    LegalSection(
                        title = "3. Spiritual Inquiries & Waleey AI",
                        content = "When consulting Waleey, queries are transmitted over encrypted TLS connections solely to retrieve authentic Islamic citations from the Quran and Sunnah. Queries are never sold, never shared with third-party data brokers, and never used to build advertising profiles."
                    )

                    LegalSection(
                        title = "4. No Advertising or Tracking SDKs",
                        content = "MoodVerses contains no ad networks, tracking cookies, or commercial telemetry SDKs. The app exists purely for authentic Islamic spiritual reflection and contemplation."
                    )

                    LegalSection(
                        title = "5. Data Control & Deletion",
                        content = "You retain full control over your data. You may clear your local favorites and conversations at any time. If you wish to delete your cloud account completely, you may do so by reaching out via our official repository."
                    )

                    LegalSection(
                        title = "6. Contact",
                        content = "For inquiries regarding your data or security, visit our official repository: github.com/zbr-khn/MoodVerses."
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Close", color = EmeraldDeep, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TermsConditionsDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(EmeraldDeep),
            color = EmeraldDeep
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Terms and Conditions",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GoldAccent)
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(EmeraldSurface, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = "MoodVerses Terms and Conditions",
                        color = GoldLight,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Effective Date: November 2026",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    LegalSection(
                        title = "1. Acceptance of Terms",
                        content = "By downloading, accessing, or using MoodVerses, you agree to comply with and be bound by these Terms and Conditions. If you do not agree to these terms, please discontinue use of the application."
                    )

                    LegalSection(
                        title = "2. Scope & Purpose",
                        content = "MoodVerses provides thematic Quranic verse selections, authentic translations, reflections, a digital Tasbih Dhikr counter, and conversational Islamic knowledge lookup through Waleey. All content is intended for devotional, contemplative, and educational purposes."
                    )

                    LegalSection(
                        title = "3. Religious Content & Scholarly Disclaimer",
                        content = "All Quranic Arabic texts and standard English translations are compiled from verified, scholarly recognized classical sources. Reflections and AI assistance are designed to aid personal contemplation. They do not constitute formal religious rulings (Fatwas). For complex theological or jurisprudence matters, users should consult qualified Islamic scholars."
                    )

                    LegalSection(
                        title = "4. User Conduct",
                        content = "Users agree to use MoodVerses with dignity and respect. Any attempt to abuse the service, disrupt server infrastructure, or use the app for unlawful purposes is strictly prohibited."
                    )

                    LegalSection(
                        title = "5. Intellectual Property",
                        content = "The Holy Quran is the heritage of the entire Muslim Ummah. The original user interface, design, typography, software architecture, and codebase of MoodVerses are protected by intellectual property rights."
                    )

                    LegalSection(
                        title = "6. Limitation of Liability",
                        content = "MoodVerses is provided on an 'as is' and 'as available' basis. While we strive for uncompromising accuracy and uptime, we make no guarantees that service will be free from temporary interruptions."
                    )

                    LegalSection(
                        title = "7. Contact",
                        content = "For questions or suggestions regarding these terms, visit github.com/zbr-khn/MoodVerses."
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Close", color = EmeraldDeep, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LegalSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            color = GoldAccent,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            color = TextSecondary,
            fontSize = 12.5.sp,
            lineHeight = 18.sp
        )
    }
}
