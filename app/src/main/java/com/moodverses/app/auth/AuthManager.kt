package com.moodverses.app.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.concurrent.TimeUnit

sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class PhoneOtpState {
    object Idle : PhoneOtpState()
    object SendingOtp : PhoneOtpState()
    data class CodeSent(val verificationId: String) : PhoneOtpState()
    data class VerificationFailed(val message: String) : PhoneOtpState()
    object VerifiedSuccess : PhoneOtpState()
}

object AuthManager {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val currentUser: FirebaseUser?
        get() = try {
            auth.currentUser
        } catch (e: Exception) {
            null
        }

    val isLoggedIn: Boolean
        get() = currentUser != null

    fun getDisplayName(): String {
        val user = currentUser ?: return "Guest"
        return when {
            !user.displayName.isNullOrBlank() -> user.displayName!!
            !user.email.isNullOrBlank() -> user.email!!.substringBefore("@")
            !user.phoneNumber.isNullOrBlank() -> user.phoneNumber!!
            else -> "Believer"
        }
    }

    /**
     * 1. Email & Password Registration
     */
    fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        onComplete: (AuthResult) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onComplete(AuthResult.Error("Please enter a valid email and password."))
            return
        }
        if (password.length < 6) {
            onComplete(AuthResult.Error("Password must be at least 6 characters long."))
            return
        }

        try {
            auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (!name.isBlank() && user != null) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name.trim())
                            .build()
                        user.updateProfile(profileUpdates)
                    }
                    onComplete(AuthResult.Success(user))
                }
                .addOnFailureListener { e ->
                    onComplete(AuthResult.Error(e.localizedMessage ?: "Sign up failed."))
                }
        } catch (e: Exception) {
            onComplete(AuthResult.Error(e.localizedMessage ?: "Authentication service unavailable."))
        }
    }

    /**
     * 2. Email & Password Login
     */
    fun signInWithEmail(
        email: String,
        password: String,
        onComplete: (AuthResult) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onComplete(AuthResult.Error("Please enter your email and password."))
            return
        }

        try {
            auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener { result ->
                    onComplete(AuthResult.Success(result.user))
                }
                .addOnFailureListener { e ->
                    onComplete(AuthResult.Error(e.localizedMessage ?: "Invalid credentials."))
                }
        } catch (e: Exception) {
            onComplete(AuthResult.Error(e.localizedMessage ?: "Authentication service unavailable."))
        }
    }

    /**
     * 3. Forgot Password / Password Reset Link
     */
    fun sendPasswordReset(
        email: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        if (email.isBlank()) {
            onComplete(false, "Please enter your registered email address.")
            return
        }

        try {
            auth.sendPasswordResetEmail(email.trim())
                .addOnSuccessListener {
                    onComplete(true, "Password reset email sent! Check your inbox.")
                }
                .addOnFailureListener { e ->
                    onComplete(false, e.localizedMessage ?: "Failed to send reset email.")
                }
        } catch (e: Exception) {
            onComplete(false, e.localizedMessage ?: "Authentication service unavailable.")
        }
    }

    /**
     * 4. Phone Number OTP: Send Verification Code
     */
    fun sendPhoneVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onStateChange: (PhoneOtpState) -> Unit
    ) {
        val trimmed = phoneNumber.trim()
        if (trimmed.length < 8) {
            onStateChange(PhoneOtpState.VerificationFailed("Please enter a valid phone number with country code (e.g. +1... or +91...)"))
            return
        }

        onStateChange(PhoneOtpState.SendingOtp)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Instant SMS verification on device
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        onStateChange(PhoneOtpState.VerifiedSuccess)
                    }
                    .addOnFailureListener { e ->
                        onStateChange(PhoneOtpState.VerificationFailed(e.localizedMessage ?: "Verification failed."))
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onStateChange(PhoneOtpState.VerificationFailed(e.localizedMessage ?: "Failed to send OTP."))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onStateChange(PhoneOtpState.CodeSent(verificationId))
            }
        }

        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(trimmed)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            onStateChange(PhoneOtpState.VerificationFailed(e.localizedMessage ?: "Phone authentication error."))
        }
    }

    /**
     * 5. Verify Phone SMS OTP Code
     */
    fun verifyOtpAndSignIn(
        verificationId: String,
        code: String,
        onComplete: (AuthResult) -> Unit
    ) {
        if (code.isBlank() || code.length < 6) {
            onComplete(AuthResult.Error("Please enter the complete 6-digit OTP code."))
            return
        }

        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code.trim())
            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    onComplete(AuthResult.Success(result.user))
                }
                .addOnFailureListener { e ->
                    onComplete(AuthResult.Error(e.localizedMessage ?: "Invalid or expired OTP code."))
                }
        } catch (e: Exception) {
            onComplete(AuthResult.Error(e.localizedMessage ?: "Verification error."))
        }
    }

    /**
     * 6. Sign in with Google ID Token credential
     */
    fun signInWithGoogleToken(
        idToken: String,
        onComplete: (AuthResult) -> Unit
    ) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    onComplete(AuthResult.Success(result.user))
                }
                .addOnFailureListener { e ->
                    onComplete(AuthResult.Error(e.localizedMessage ?: "Google sign in failed."))
                }
        } catch (e: Exception) {
            onComplete(AuthResult.Error(e.localizedMessage ?: "Google sign in error."))
        }
    }

    /**
     * Sign Out
     */
    fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
