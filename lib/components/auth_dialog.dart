import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../theme/app_theme.dart';

class AuthDialog extends StatefulWidget {
  final VoidCallback? onDismiss;
  final VoidCallback? onAuthSuccess;
  final VoidCallback? onSuccess;

  const AuthDialog({
    super.key,
    this.onDismiss,
    this.onAuthSuccess,
    this.onSuccess,
  });

  @override
  State<AuthDialog> createState() => _AuthDialogState();
}

class _AuthDialogState extends State<AuthDialog> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  // Phone OTP
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _otpController = TextEditingController();
  String? _verificationId;
  bool _isSendingOtp = false;
  bool _isVerifyingOtp = false;

  // Email / Pass
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _nameController = TextEditingController();
  bool _isSignUpMode = false;
  bool _isLoading = false;

  String? _errorMessage;
  String? _successMessage;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _phoneController.dispose();
    _otpController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _nameController.dispose();
    super.dispose();
  }

  void _sendPhoneOtp() async {
    final phone = _phoneController.text.trim();
    if (phone.length < 8) {
      setState(() => _errorMessage = "Please enter a valid phone number with country code (+1... or +91...)");
      return;
    }

    setState(() {
      _isSendingOtp = true;
      _errorMessage = null;
      _successMessage = null;
    });

    await AuthService.sendPhoneOtp(
      phoneNumber: phone,
      onCodeSent: (verificationId) {
        setState(() {
          _isSendingOtp = false;
          _verificationId = verificationId;
          _successMessage = "6-digit OTP code sent! Enter below:";
        });
      },
      onError: (error) {
        setState(() {
          _isSendingOtp = false;
          _errorMessage = error;
        });
      },
    );
  }

  String _resolveGreetingName(dynamic user, [String? fallback]) {
    if (fallback != null && fallback.trim().isNotEmpty) {
      return fallback.trim();
    }
    if (user != null) {
      final dn = user.displayName as String?;
      if (dn != null && dn.trim().isNotEmpty) {
        return dn.trim();
      }
      final email = user.email as String?;
      if (email != null && email.contains('@')) {
        final prefix = email.split('@').first;
        if (prefix.isNotEmpty) {
          return prefix[0].toUpperCase() + prefix.substring(1);
        }
      }
      final phone = user.phoneNumber as String?;
      if (phone != null && phone.isNotEmpty) {
        return phone;
      }
    }
    return "Believer";
  }

  void _notifySuccess(dynamic user, bool isNewUser, [String? fallbackName]) {
    final name = _resolveGreetingName(user, fallbackName);
    final message = isNewUser
        ? "Welcome, $name! 🌟 Your account has been created."
        : "Welcome back, $name! 🌸";

    final messenger = ScaffoldMessenger.maybeOf(context);
    messenger?.showSnackBar(
      SnackBar(
        content: Row(
          children: [
            const Icon(Icons.check_circle, color: AppColors.emeraldPrimary, size: 20),
            const SizedBox(width: 10),
            Expanded(child: Text(message, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold))),
          ],
        ),
        backgroundColor: AppColors.emeraldSurfaceVariant,
        behavior: SnackBarBehavior.floating,
        duration: const Duration(seconds: 4),
      ),
    );
  }

  void _verifyOtp() async {
    final code = _otpController.text.trim();
    if (code.length < 6 || _verificationId == null) {
      setState(() => _errorMessage = "Please enter the complete 6-digit code.");
      return;
    }

    setState(() {
      _isVerifyingOtp = true;
      _errorMessage = null;
    });

    final res = await AuthService.verifyOtpAndSignIn(_verificationId!, code);
    setState(() => _isVerifyingOtp = false);

    if (res.success) {
      _notifySuccess(res.user, false);
      (widget.onAuthSuccess ?? widget.onSuccess)?.call();
      Navigator.of(context).pop();
    } else {
      setState(() => _errorMessage = res.errorMessage);
    }
  }

  void _handleEmailAuth() async {
    final email = _emailController.text.trim();
    final password = _passwordController.text.trim();
    final nameInput = _nameController.text.trim();

    if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(email)) {
      setState(() => _errorMessage = "Please enter a valid email address.");
      return;
    }
    if (password.length < 6) {
      setState(() => _errorMessage = "Password must be at least 6 characters.");
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    final AuthResult res;
    if (_isSignUpMode) {
      res = await AuthService.signUpWithEmail(email, password, nameInput);
    } else {
      res = await AuthService.signInWithEmail(email, password);
    }

    setState(() => _isLoading = false);

    if (res.success) {
      _notifySuccess(res.user, _isSignUpMode, nameInput);
      (widget.onAuthSuccess ?? widget.onSuccess)?.call();
      Navigator.of(context).pop();
    } else {
      setState(() => _errorMessage = res.errorMessage);
    }
  }

  void _handleGoogleSignIn() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    final res = await AuthService.signInWithGoogle();
    setState(() => _isLoading = false);

    if (res.success) {
      _notifySuccess(res.user, false);
      (widget.onAuthSuccess ?? widget.onSuccess)?.call();
      Navigator.of(context).pop();
    } else {
      setState(() => _errorMessage = res.errorMessage);
    }
  }

  void _showForgotPasswordDialog() {
    final forgotController = TextEditingController(text: _emailController.text.trim());
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: AppColors.emeraldSurface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        title: const Text("Reset Password", style: TextStyle(color: AppColors.textPrimary)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text(
              "Enter your registered email address and we'll send you a password reset link.",
              style: TextStyle(color: AppColors.textSecondary, fontSize: 13),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: forgotController,
              decoration: const InputDecoration(
                hintText: "email@example.com",
                labelText: "Email",
                border: OutlineInputBorder(),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text("Cancel", style: TextStyle(color: AppColors.textMuted)),
          ),
          ElevatedButton(
            onPressed: () async {
              final err = await AuthService.sendPasswordReset(forgotController.text.trim());
              Navigator.pop(ctx);
              setState(() {
                if (err == null) {
                  _successMessage = "Password reset link sent! Check your inbox.";
                } else {
                  _errorMessage = err;
                }
              });
            },
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.emeraldPrimary),
            child: const Text("Send Link", style: TextStyle(color: AppColors.emeraldDeep, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: AppColors.emeraldDeep,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
        side: const BorderSide(color: AppColors.borderSubtle),
      ),
      insetPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("MoodVerses Account", style: TextStyle(color: AppColors.textPrimary, fontSize: 17, fontWeight: FontWeight.bold)),
                    Text("Cloud Sync & Multi-Device Access", style: TextStyle(color: AppColors.goldLight, fontSize: 11)),
                  ],
                ),
                IconButton(
                  onPressed: () {
                    if (widget.onDismiss != null) {
                      widget.onDismiss!();
                    } else {
                      Navigator.of(context).pop();
                    }
                  },
                  icon: const Icon(Icons.close, color: AppColors.textMuted),
                ),
              ],
            ),
            const SizedBox(height: 12),

            // Tab Bar
            TabBar(
              controller: _tabController,
              indicatorColor: AppColors.goldAccent,
              labelColor: AppColors.goldAccent,
              unselectedLabelColor: AppColors.textMuted,
              tabs: const [
                Tab(icon: Icon(Icons.phone_android, size: 18), text: "Phone OTP"),
                Tab(icon: Icon(Icons.email, size: 18), text: "Email & Pass"),
              ],
            ),
            const SizedBox(height: 16),

            // Error / Success Banners
            if (_errorMessage != null)
              Container(
                margin: const EdgeInsets.only(bottom: 12),
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: Colors.red.withOpacity(0.15),
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(color: Colors.red.withOpacity(0.3)),
                ),
                child: Text(_errorMessage!, style: const TextStyle(color: Color(0xFFFCA5A5), fontSize: 12), textAlign: TextAlign.center),
              ),

            if (_successMessage != null)
              Container(
                margin: const EdgeInsets.only(bottom: 12),
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: Colors.green.withOpacity(0.15),
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(color: Colors.green.withOpacity(0.3)),
                ),
                child: Text(_successMessage!, style: const TextStyle(color: Color(0xFFA7F3D0), fontSize: 12), textAlign: TextAlign.center),
              ),

            // Tab Content
            SizedBox(
              height: _isSignUpMode && _tabController.index == 1 ? 270 : 210,
              child: TabBarView(
                controller: _tabController,
                children: [
                  // Phone OTP Tab
                  Column(
                    children: [
                      if (_verificationId == null) ...[
                        const Text(
                          "Sign in with your mobile number — no password needed. We'll send an SMS verification code.",
                          style: TextStyle(color: AppColors.textSecondary, fontSize: 12),
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(height: 14),
                        TextField(
                          controller: _phoneController,
                          keyboardType: TextInputType.phone,
                          decoration: const InputDecoration(
                            labelText: "Phone Number (+CountryCode)",
                            hintText: "+1... or +91...",
                            prefixIcon: Icon(Icons.phone, color: AppColors.goldAccent, size: 18),
                            border: OutlineInputBorder(),
                          ),
                        ),
                        const SizedBox(height: 14),
                        SizedBox(
                          width: double.infinity,
                          height: 44,
                          child: ElevatedButton(
                            onPressed: _isSendingOtp ? null : _sendPhoneOtp,
                            style: ElevatedButton.styleFrom(backgroundColor: AppColors.emeraldPrimary),
                            child: _isSendingOtp
                                ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.emeraldDeep))
                                : const Text("Send Verification OTP", style: TextStyle(color: AppColors.emeraldDeep, fontWeight: FontWeight.bold)),
                          ),
                        ),
                      ] else ...[
                        Text(
                          "Enter the 6-digit code sent to ${_phoneController.text.trim()}:",
                          style: const TextStyle(color: AppColors.goldLight, fontSize: 12.5),
                        ),
                        const SizedBox(height: 14),
                        TextField(
                          controller: _otpController,
                          keyboardType: TextInputType.number,
                          textAlign: TextAlign.center,
                          style: const TextStyle(fontSize: 20, letterSpacing: 8, fontWeight: FontWeight.bold),
                          decoration: const InputDecoration(
                            hintText: "123456",
                            border: OutlineInputBorder(),
                          ),
                        ),
                        const SizedBox(height: 14),
                        SizedBox(
                          width: double.infinity,
                          height: 44,
                          child: ElevatedButton(
                            onPressed: _isVerifyingOtp ? null : _verifyOtp,
                            style: ElevatedButton.styleFrom(backgroundColor: AppColors.emeraldPrimary),
                            child: _isVerifyingOtp
                                ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.emeraldDeep))
                                : const Text("Verify & Sign In", style: TextStyle(color: AppColors.emeraldDeep, fontWeight: FontWeight.bold)),
                          ),
                        ),
                      ],
                    ],
                  ),

                  // Email & Password Tab
                  Column(
                    children: [
                      if (_isSignUpMode) ...[
                        TextField(
                          controller: _nameController,
                          decoration: const InputDecoration(
                            labelText: "Full Name",
                            prefixIcon: Icon(Icons.person, color: AppColors.goldAccent, size: 18),
                            border: OutlineInputBorder(),
                          ),
                        ),
                        const SizedBox(height: 10),
                      ],
                      TextField(
                        controller: _emailController,
                        keyboardType: TextInputType.emailAddress,
                        decoration: const InputDecoration(
                          labelText: "Email Address",
                          prefixIcon: Icon(Icons.email, color: AppColors.goldAccent, size: 18),
                          border: OutlineInputBorder(),
                        ),
                      ),
                      const SizedBox(height: 10),
                      TextField(
                        controller: _passwordController,
                        obscureText: true,
                        decoration: const InputDecoration(
                          labelText: "Password (min 6 chars)",
                          prefixIcon: Icon(Icons.lock, color: AppColors.goldAccent, size: 18),
                          border: OutlineInputBorder(),
                        ),
                      ),
                      if (!_isSignUpMode) ...[
                        Align(
                          alignment: Alignment.centerRight,
                          child: TextButton(
                            onPressed: _showForgotPasswordDialog,
                            child: const Text("Forgot Password?", style: TextStyle(color: AppColors.goldLight, fontSize: 11)),
                          ),
                        ),
                      ] else ...[
                        const SizedBox(height: 12),
                      ],
                      SizedBox(
                        width: double.infinity,
                        height: 44,
                        child: ElevatedButton(
                          onPressed: _isLoading ? null : _handleEmailAuth,
                          style: ElevatedButton.styleFrom(backgroundColor: AppColors.emeraldPrimary),
                          child: _isLoading
                              ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.emeraldDeep))
                              : Text(_isSignUpMode ? "Create Account" : "Sign In", style: const TextStyle(color: AppColors.emeraldDeep, fontWeight: FontWeight.bold)),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),

            // Toggle mode for Email
            if (_tabController.index == 1)
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(_isSignUpMode ? "Already have an account?" : "New to MoodVerses?", style: const TextStyle(color: AppColors.textMuted, fontSize: 12)),
                  TextButton(
                    onPressed: () => setState(() => _isSignUpMode = !_isSignUpMode),
                    child: Text(_isSignUpMode ? "Sign In" : "Create one", style: const TextStyle(color: AppColors.goldAccent, fontWeight: FontWeight.bold, fontSize: 12)),
                  ),
                ],
              ),

            const Divider(color: AppColors.borderSubtle),
            const SizedBox(height: 10),

            // Google Sign-In Button
            SizedBox(
              width: double.infinity,
              height: 44,
              child: OutlinedButton.icon(
                onPressed: _isLoading ? null : _handleGoogleSignIn,
                style: OutlinedButton.styleFrom(
                  backgroundColor: AppColors.emeraldSurfaceVariant,
                  side: const BorderSide(color: AppColors.borderSubtle),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
                icon: const Icon(Icons.account_circle, color: AppColors.goldAccent),
                label: const Text("Continue with Google", style: TextStyle(color: AppColors.textPrimary)),
              ),
            ),
            const SizedBox(height: 6),

            // Guest option
            TextButton(
              onPressed: () {
                if (widget.onDismiss != null) {
                  widget.onDismiss!();
                } else {
                  Navigator.of(context).pop();
                }
              },
              child: const Text("Continue as Guest (Offline Only)", style: TextStyle(color: AppColors.textMuted, fontSize: 11.5)),
            ),
          ],
        ),
      ),
    );
  }
}
