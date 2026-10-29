import 'package:flutter/foundation.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AuthResult {
  final bool success;
  final User? user;
  final String? errorMessage;

  AuthResult.success(this.user)
      : success = true,
        errorMessage = null;

  AuthResult.error(this.errorMessage)
      : success = false,
        user = null;
}

class AuthService {
  static FirebaseAuth get _auth => FirebaseAuth.instance;

  static const String _userNameKey = 'user_custom_name';

  static final GoogleSignIn _googleSignIn = GoogleSignIn(
    serverClientId: "989734168953-8e895cqm63hnrpt6g4tippnjeia1etfb.apps.googleusercontent.com",
  );

  static User? get currentUser {
    try {
      return _auth.currentUser;
    } catch (_) {
      return null;
    }
  }

  static bool get isLoggedIn => currentUser != null;

  static Stream<User?> get authStateChanges {
    try {
      return _auth.authStateChanges();
    } catch (_) {
      return const Stream.empty();
    }
  }

  // Retrieve current user display name or locally saved custom name
  static Future<String?> getUserName() async {
    try {
      final user = currentUser;
      if (user?.displayName != null && user!.displayName!.trim().isNotEmpty) {
        return user.displayName!.trim();
      }
      final prefs = await SharedPreferences.getInstance();
      final local = prefs.getString(_userNameKey);
      if (local != null && local.trim().isNotEmpty) {
        return local.trim();
      }
    } catch (_) {}
    return null;
  }

  // Save/update user display name in SharedPreferences and Firebase Auth
  static Future<void> saveUserName(String name) async {
    final clean = name.trim();
    try {
      final prefs = await SharedPreferences.getInstance();
      if (clean.isEmpty) {
        await prefs.remove(_userNameKey);
      } else {
        await prefs.setString(_userNameKey, clean);
      }
      final user = currentUser;
      if (user != null && clean.isNotEmpty) {
        await user.updateDisplayName(clean);
      }
    } catch (_) {}
  }

  // 1. Mobile Phone Number Verification
  static Future<void> sendPhoneOtp({
    required String phoneNumber,
    required Function(String verificationId) onCodeSent,
    required Function(String error) onError,
  }) async {
    try {
      await _auth.verifyPhoneNumber(
        phoneNumber: phoneNumber.trim(),
        verificationCompleted: (PhoneAuthCredential credential) async {
          await _auth.signInWithCredential(credential);
        },
        verificationFailed: (FirebaseAuthException e) {
          final msg = e.message ?? "Phone verification failed.";
          if (msg.contains("BILLING_NOT_ENABLED")) {
            onError("Real carrier SMS requires a billing setup in Firebase. You can test with test numbers or use Google/Email sign in.");
          } else {
            onError(msg);
          }
        },
        codeSent: (String verificationId, int? resendToken) {
          onCodeSent(verificationId);
        },
        codeAutoRetrievalTimeout: (String verificationId) {},
      );
    } catch (e) {
      onError(e.toString());
    }
  }

  static Future<AuthResult> verifyOtpAndSignIn(String verificationId, String smsCode) async {
    try {
      final credential = PhoneAuthProvider.credential(
        verificationId: verificationId,
        smsCode: smsCode.trim(),
      );
      final userCredential = await _auth.signInWithCredential(credential);
      final user = userCredential.user;
      final localName = await getUserName();
      if (localName != null && localName.trim().isNotEmpty && (user?.displayName == null || user!.displayName!.isEmpty)) {
        try {
          await user?.updateDisplayName(localName.trim());
        } catch (_) {}
      }
      return AuthResult.success(user);
    } on FirebaseAuthException catch (e) {
      return AuthResult.error(e.message ?? "Invalid verification code.");
    } catch (e) {
      return AuthResult.error(e.toString());
    }
  }

  // 2. Google Sign-In (Cross-Platform Web & Mobile with Null-Safety)
  static Future<AuthResult> signInWithGoogle() async {
    try {
      if (kIsWeb) {
        final GoogleAuthProvider googleProvider = GoogleAuthProvider();
        googleProvider.addScope('email');
        googleProvider.setCustomParameters({'prompt': 'select_account'});
        final userCredential = await _auth.signInWithPopup(googleProvider);
        final user = userCredential.user;
        if (user?.displayName != null && user!.displayName!.trim().isNotEmpty) {
          await saveUserName(user.displayName!.trim());
        }
        return AuthResult.success(user);
      } else {
        final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
        if (googleUser == null) {
          return AuthResult.error("Sign in cancelled by user.");
        }

        final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
        if (googleAuth.idToken == null && googleAuth.accessToken == null) {
          return AuthResult.error("Could not obtain Google authentication token.");
        }

        final OAuthCredential credential = GoogleAuthProvider.credential(
          accessToken: googleAuth.accessToken,
          idToken: googleAuth.idToken,
        );

        final userCredential = await _auth.signInWithCredential(credential);
        final user = userCredential.user;
        if (user?.displayName != null && user!.displayName!.trim().isNotEmpty) {
          await saveUserName(user.displayName!.trim());
        }
        return AuthResult.success(user);
      }
    } on FirebaseAuthException catch (e) {
      return AuthResult.error(e.message ?? "Google sign in failed.");
    } catch (e) {
      return AuthResult.error(e.toString());
    }
  }

  // 3. Email & Password Sign Up
  static Future<AuthResult> signUpWithEmail(String email, String password, String name) async {
    try {
      final userCredential = await _auth.createUserWithEmailAndPassword(
        email: email.trim(),
        password: password.trim(),
      );
      if (name.trim().isNotEmpty) {
        await userCredential.user?.updateDisplayName(name.trim());
        await saveUserName(name.trim());
      }
      return AuthResult.success(userCredential.user);
    } on FirebaseAuthException catch (e) {
      return AuthResult.error(e.message ?? "Registration failed.");
    } catch (e) {
      return AuthResult.error(e.toString());
    }
  }

  // 4. Email & Password Sign In
  static Future<AuthResult> signInWithEmail(String email, String password) async {
    try {
      final userCredential = await _auth.signInWithEmailAndPassword(
        email: email.trim(),
        password: password.trim(),
      );
      return AuthResult.success(userCredential.user);
    } on FirebaseAuthException catch (e) {
      return AuthResult.error(e.message ?? "Invalid email or password.");
    } catch (e) {
      return AuthResult.error(e.toString());
    }
  }

  // 5. Password Reset
  static Future<String?> sendPasswordReset(String email) async {
    try {
      await _auth.sendPasswordResetEmail(email: email.trim());
      return null; // Null means success
    } on FirebaseAuthException catch (e) {
      return e.message ?? "Failed to send reset link.";
    } catch (e) {
      return e.toString();
    }
  }

  // 6. Sign Out
  static Future<void> signOut() async {
    await _auth.signOut();
    await _googleSignIn.signOut();
  }
}
