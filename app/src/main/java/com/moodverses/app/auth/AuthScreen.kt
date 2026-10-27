package com.moodverses.app.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.moodverses.app.R
import com.moodverses.app.ui.theme.*

@Composable
fun AuthDialog(
    onDismiss: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Mobile OTP, 1: Email/Password

    // Email & Password States
    var isSignUpMode by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmailInput by remember { mutableStateOf("") }

    // Phone OTP States
    var phoneNumberInput by remember { mutableStateOf("") }
    var otpCodeInput by remember { mutableStateOf("") }
    var phoneOtpState by remember { mutableStateOf<PhoneOtpState>(PhoneOtpState.Idle) }

    // Feedback messages
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val webClientId = "989734168953-8e895cqm63hnrpt6g4tippnjeia1etfb.apps.googleusercontent.com"
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (!idToken.isNullOrBlank()) {
                isLoading = true
                AuthManager.signInWithGoogleToken(idToken) { res ->
                    isLoading = false
                    when (res) {
                        is AuthResult.Success -> onAuthSuccess()
                        is AuthResult.Error -> errorMessage = res.message
                    }
                }
            } else {
                errorMessage = "Could not retrieve Google sign-in credentials."
            }
        } catch (e: ApiException) {
            if (e.statusCode != 12501) { // 12501: User dismissed Google account picker
                errorMessage = "Google sign-in error (${e.statusCode}): ${e.localizedMessage}"
            }
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            containerColor = EmeraldSurface,
            title = {
                Text("Reset Password", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    Text(
                        "Enter your registered email address and we'll send you a password reset link.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        label = { Text("Email Address", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        AuthManager.sendPasswordReset(forgotEmailInput) { success, msg ->
                            isLoading = false
                            if (success) {
                                successMessage = msg
                                showForgotPasswordDialog = false
                            } else {
                                errorMessage = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Send Reset Link", color = EmeraldDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo & Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                                text = "MoodVerses Account",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cloud Chat Sync & Personalization",
                                color = GoldLight,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = EmeraldSurfaceVariant,
                    contentColor = GoldAccent,
                    divider = {},
                    indicator = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                            successMessage = null
                        },
                        text = {
                            Text(
                                "📱 Phone OTP",
                                color = if (selectedTab == 0) GoldAccent else TextMuted,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.5.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                            successMessage = null
                        },
                        text = {
                            Text(
                                "✉️ Email & Pass",
                                color = if (selectedTab == 1) GoldAccent else TextMuted,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.5.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error / Success Banner
                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        color = Color(0x33EF4444),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFFCA5A5),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (!successMessage.isNullOrBlank()) {
                    Surface(
                        color = Color(0x3310B981),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = successMessage!!,
                            color = Color(0xFFA7F3D0),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // TAB 0: PHONE NUMBER WITH OTP
                if (selectedTab == 0) {
                    when (val state = phoneOtpState) {
                        is PhoneOtpState.Idle, is PhoneOtpState.SendingOtp, is PhoneOtpState.VerificationFailed -> {
                            Text(
                                text = "Sign in directly with your mobile number. No password required — we will send an SMS with a 6-digit verification code.",
                                color = TextSecondary,
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = phoneNumberInput,
                                onValueChange = { phoneNumberInput = it },
                                label = { Text("Phone Number (+CountryCode)", color = TextMuted) },
                                placeholder = { Text("+1... or +91...", color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldAccent) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (activity != null) {
                                        errorMessage = null
                                        AuthManager.sendPhoneVerificationCode(phoneNumberInput, activity) { newState ->
                                            phoneOtpState = newState
                                            if (newState is PhoneOtpState.VerificationFailed) {
                                                val raw = newState.message
                                                errorMessage = if (raw.contains("API key not valid", ignoreCase = true)) {
                                                    "Firebase Setup Required: The app currently uses a template google-services.json. Download your project's google-services.json from console.firebase.google.com to enable real SMS OTPs."
                                                } else {
                                                    raw
                                                }
                                            } else if (newState is PhoneOtpState.VerifiedSuccess) {
                                                onAuthSuccess()
                                            }
                                        }
                                    }
                                },
                                enabled = phoneNumberInput.isNotBlank() && phoneOtpState !is PhoneOtpState.SendingOtp,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                if (phoneOtpState is PhoneOtpState.SendingOtp) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = EmeraldDeep)
                                } else {
                                    Text("Send Verification OTP", color = EmeraldDeep, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        is PhoneOtpState.CodeSent -> {
                            Text(
                                text = "OTP code sent to $phoneNumberInput. Enter the 6-digit code below:",
                                color = GoldLight,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = otpCodeInput,
                                onValueChange = { if (it.length <= 6) otpCodeInput = it },
                                label = { Text("6-Digit OTP Code", color = TextMuted) },
                                placeholder = { Text("123456", color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = GoldAccent) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldAccent,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    isLoading = true
                                    AuthManager.verifyOtpAndSignIn(state.verificationId, otpCodeInput) { result ->
                                        isLoading = false
                                        when (result) {
                                            is AuthResult.Success -> onAuthSuccess()
                                            is AuthResult.Error -> errorMessage = result.message
                                        }
                                    }
                                },
                                enabled = otpCodeInput.length == 6 && !isLoading,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = EmeraldDeep)
                                } else {
                                    Text("Verify & Sign In", color = EmeraldDeep, fontWeight = FontWeight.Bold)
                                }
                            }

                            TextButton(
                                onClick = { phoneOtpState = PhoneOtpState.Idle }
                            ) {
                                Text("Change Phone Number", color = TextMuted, fontSize = 12.sp)
                            }
                        }

                        is PhoneOtpState.VerifiedSuccess -> {
                            LaunchedEffect(Unit) {
                                onAuthSuccess()
                            }
                        }
                    }
                }

                // TAB 1: EMAIL & PASSWORD (SIGN IN / SIGN UP)
                if (selectedTab == 1) {
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Your Name", color = TextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password", color = TextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!isSignUpMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    forgotEmailInput = emailInput
                                    showForgotPasswordDialog = true
                                }
                            ) {
                                Text("Forgot Password?", color = GoldLight, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            val cleanEmail = emailInput.trim()
                            val cleanPassword = passwordInput.trim()

                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                                errorMessage = "Please enter a valid email address."
                                return@Button
                            }
                            if (cleanPassword.length < 6) {
                                errorMessage = "Password must be at least 6 characters long."
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null
                            if (isSignUpMode) {
                                AuthManager.signUpWithEmail(cleanEmail, cleanPassword, nameInput.trim()) { res ->
                                    isLoading = false
                                    when (res) {
                                        is AuthResult.Success -> onAuthSuccess()
                                        is AuthResult.Error -> {
                                            val raw = res.message
                                            errorMessage = if (raw.contains("API key not valid", ignoreCase = true)) {
                                                "Firebase Setup Required: The app is using a template google-services.json. Download your real google-services.json from console.firebase.google.com to activate cloud accounts."
                                            } else {
                                                raw
                                            }
                                        }
                                    }
                                }
                            } else {
                                AuthManager.signInWithEmail(cleanEmail, cleanPassword) { res ->
                                    isLoading = false
                                    when (res) {
                                        is AuthResult.Success -> onAuthSuccess()
                                        is AuthResult.Error -> {
                                            val raw = res.message
                                            errorMessage = if (raw.contains("API key not valid", ignoreCase = true)) {
                                                "Firebase Setup Required: The app is using a template google-services.json. Download your real google-services.json from console.firebase.google.com to activate cloud accounts."
                                            } else {
                                                raw
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        enabled = emailInput.isNotBlank() && passwordInput.isNotBlank() && !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = EmeraldDeep)
                        } else {
                            Text(
                                text = if (isSignUpMode) "Create Account" else "Sign In",
                                color = EmeraldDeep,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUpMode) "Already have an account?" else "Don't have an account?",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        TextButton(
                            onClick = {
                                isSignUpMode = !isSignUpMode
                                errorMessage = null
                            }
                        ) {
                            Text(
                                text = if (isSignUpMode) "Sign In" else "Create one",
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // GOOGLE SIGN IN BUTTON
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldSurfaceVariant,
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clickable {
                            errorMessage = null
                            googleLauncher.launch(googleSignInClient.signInIntent)
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Google",
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Guest option
                TextButton(onClick = onDismiss) {
                    Text("Continue as Guest (Offline Only)", color = TextMuted, fontSize = 11.5.sp)
                }
            }
        }
    }
}
