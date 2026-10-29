import 'package:flutter/material.dart';
import '../services/auth_service.dart';
import '../theme/app_theme.dart';
import 'auth_dialog.dart';

class UserProfileDialog extends StatefulWidget {
  final VoidCallback? onProfileUpdated;

  const UserProfileDialog({
    super.key,
    this.onProfileUpdated,
  });

  static Future<void> show(BuildContext context, {VoidCallback? onProfileUpdated}) {
    return showDialog(
      context: context,
      builder: (ctx) => UserProfileDialog(onProfileUpdated: onProfileUpdated),
    );
  }

  @override
  State<UserProfileDialog> createState() => _UserProfileDialogState();
}

class _UserProfileDialogState extends State<UserProfileDialog> {
  final TextEditingController _nameController = TextEditingController();
  bool _isLoading = true;
  bool _isSavingName = false;
  String? _feedbackMessage;

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  void _loadUserData() async {
    final name = await AuthService.getUserName();
    if (mounted) {
      setState(() {
        _nameController.text = name ?? "";
        _isLoading = false;
      });
    }
  }

  void _saveName() async {
    final newName = _nameController.text.trim();
    if (newName.isEmpty) {
      setState(() => _feedbackMessage = "Please enter a valid name.");
      return;
    }

    setState(() {
      _isSavingName = true;
      _feedbackMessage = null;
    });

    await AuthService.saveUserName(newName);

    if (mounted) {
      setState(() {
        _isSavingName = false;
        _feedbackMessage = "Name saved as \"$newName\"! Waleey will now greet you by name.";
      });
      widget.onProfileUpdated?.call();
    }
  }

  void _openAuthDialog() {
    Navigator.of(context).pop();
    showDialog(
      context: context,
      builder: (ctx) => AuthDialog(
        onSuccess: () {
          widget.onProfileUpdated?.call();
          // Reopen profile dialog to show updated state
          if (mounted) {
            UserProfileDialog.show(context, onProfileUpdated: widget.onProfileUpdated);
          }
        },
      ),
    );
  }

  void _confirmLogout() {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: AppColors.emeraldSurface,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
          side: const BorderSide(color: AppColors.borderSubtle),
        ),
        title: const Text(
          "Log Out?",
          style: TextStyle(color: AppColors.textPrimary, fontWeight: FontWeight.bold),
        ),
        content: const Text(
          "Are you sure you want to log out from MoodVerses?",
          style: TextStyle(color: AppColors.textSecondary, fontSize: 13.5),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text("Cancel", style: TextStyle(color: AppColors.textMuted)),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.redAccent.shade700,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
            ),
            onPressed: () async {
              Navigator.pop(ctx); // Close confirmation
              Navigator.pop(context); // Close profile dialog
              await AuthService.signOut();
              widget.onProfileUpdated?.call();

              if (mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Row(
                      children: [
                        Icon(Icons.info_outline, color: Colors.white, size: 18),
                        SizedBox(width: 8),
                        Text("You are logged out.", style: TextStyle(fontWeight: FontWeight.bold)),
                      ],
                    ),
                    backgroundColor: AppColors.emeraldSurfaceVariant,
                    behavior: SnackBarBehavior.floating,
                    duration: Duration(seconds: 3),
                  ),
                );
              }
            },
            child: const Text("Yes, Log Out", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isLoggedIn = AuthService.isLoggedIn;
    final user = AuthService.currentUser;
    final currentName = _nameController.text.trim();
    final initialLetter = currentName.isNotEmpty
        ? currentName[0].toUpperCase()
        : (user?.email?.isNotEmpty == true ? user!.email![0].toUpperCase() : "M");

    return Dialog(
      backgroundColor: AppColors.emeraldSurface,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(20),
        side: const BorderSide(color: AppColors.borderSubtle),
      ),
      insetPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 420),
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(22),
          child: _isLoading
              ? const Center(
                  child: Padding(
                    padding: EdgeInsets.all(32),
                    child: CircularProgressIndicator(color: AppColors.goldAccent),
                  ),
                )
              : Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    // Header Row
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Row(
                          children: [
                            Icon(Icons.badge_outlined, color: AppColors.goldAccent, size: 22),
                            SizedBox(width: 8),
                            Text(
                              "User Profile",
                              style: TextStyle(
                                color: AppColors.textPrimary,
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),
                        IconButton(
                          icon: const Icon(Icons.close, color: AppColors.textMuted, size: 20),
                          onPressed: () => Navigator.of(context).pop(),
                          padding: EdgeInsets.zero,
                          constraints: const BoxConstraints(),
                        ),
                      ],
                    ),

                    const SizedBox(height: 18),

                    // Avatar & Status Badge
                    Center(
                      child: Column(
                        children: [
                          Container(
                            width: 64,
                            height: 64,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              gradient: const LinearGradient(
                                colors: [AppColors.emeraldPrimary, AppColors.goldAccent],
                                begin: Alignment.topLeft,
                                end: Alignment.bottomRight,
                              ),
                              boxShadow: [
                                BoxShadow(
                                  color: AppColors.emeraldPrimary.withValues(alpha: 0.3),
                                  blurRadius: 12,
                                  offset: const Offset(0, 4),
                                ),
                              ],
                            ),
                            alignment: Alignment.center,
                            child: Text(
                              initialLetter,
                              style: const TextStyle(
                                color: AppColors.emeraldDeep,
                                fontSize: 26,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                          const SizedBox(height: 10),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                            decoration: BoxDecoration(
                              color: isLoggedIn
                                  ? AppColors.emeraldPrimary.withValues(alpha: 0.15)
                                  : AppColors.goldAccent.withValues(alpha: 0.15),
                              borderRadius: BorderRadius.circular(12),
                              border: Border.all(
                                color: isLoggedIn ? AppColors.emeraldPrimary : AppColors.goldAccent,
                                width: 0.8,
                              ),
                            ),
                            child: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Icon(
                                  isLoggedIn ? Icons.cloud_done : Icons.cloud_off,
                                  size: 13,
                                  color: isLoggedIn ? AppColors.emeraldPrimary : AppColors.goldLight,
                                ),
                                const SizedBox(width: 5),
                                Text(
                                  isLoggedIn ? "Cloud Sync Active" : "Guest Account (Local)",
                                  style: TextStyle(
                                    color: isLoggedIn ? AppColors.emeraldPrimary : AppColors.goldLight,
                                    fontSize: 11,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),

                    const SizedBox(height: 20),

                    // Enter / Edit Name Section
                    const Text(
                      "Your Name (Greeting for Waleey AI)",
                      style: TextStyle(
                        color: AppColors.goldLight,
                        fontSize: 12.5,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 6),
                    Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _nameController,
                            style: const TextStyle(color: AppColors.textPrimary, fontSize: 14),
                            decoration: InputDecoration(
                              hintText: "Enter your name (e.g. Zubair)",
                              hintStyle: const TextStyle(color: AppColors.textMuted, fontSize: 13),
                              prefixIcon: const Icon(Icons.person_outline, color: AppColors.goldAccent, size: 18),
                              filled: true,
                              fillColor: AppColors.emeraldSurfaceVariant,
                              contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(12),
                                borderSide: const BorderSide(color: AppColors.borderSubtle),
                              ),
                              focusedBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(12),
                                borderSide: const BorderSide(color: AppColors.goldAccent),
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                        ElevatedButton(
                          onPressed: _isSavingName ? null : _saveName,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: AppColors.emeraldPrimary,
                            foregroundColor: AppColors.emeraldDeep,
                            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 13),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          ),
                          child: _isSavingName
                              ? const SizedBox(
                                  width: 16,
                                  height: 16,
                                  child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.emeraldDeep),
                                )
                              : const Text("Save", style: TextStyle(fontWeight: FontWeight.bold)),
                        ),
                      ],
                    ),

                    if (_feedbackMessage != null) ...[
                      const SizedBox(height: 8),
                      Text(
                        _feedbackMessage!,
                        style: TextStyle(
                          color: _feedbackMessage!.contains("saved")
                              ? AppColors.emeraldPrimary
                              : Colors.redAccent,
                          fontSize: 11.5,
                        ),
                      ),
                    ],

                    const SizedBox(height: 18),
                    const Divider(color: AppColors.borderSubtle),
                    const SizedBox(height: 12),

                    // Account Scenario Section
                    if (isLoggedIn) ...[
                      // Logged-in details
                      Container(
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: AppColors.emeraldSurfaceVariant,
                          borderRadius: BorderRadius.circular(12),
                          border: Border.all(color: AppColors.borderSubtle),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            if (user?.email != null && user!.email!.isNotEmpty)
                              Padding(
                                padding: const EdgeInsets.only(bottom: 4),
                                child: Row(
                                  children: [
                                    const Icon(Icons.email_outlined, size: 14, color: AppColors.textMuted),
                                    const SizedBox(width: 6),
                                    Expanded(
                                      child: Text(
                                        user.email!,
                                        style: const TextStyle(color: AppColors.textSecondary, fontSize: 12),
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            if (user?.phoneNumber != null && user!.phoneNumber!.isNotEmpty)
                              Padding(
                                padding: const EdgeInsets.only(bottom: 4),
                                child: Row(
                                  children: [
                                    const Icon(Icons.phone_outlined, size: 14, color: AppColors.textMuted),
                                    const SizedBox(width: 6),
                                    Text(
                                      user.phoneNumber!,
                                      style: const TextStyle(color: AppColors.textSecondary, fontSize: 12),
                                    ),
                                  ],
                                ),
                              ),
                            const SizedBox(height: 4),
                            const Text(
                              "All your liked Ayat and Waleey conversations are securely backed up.",
                              style: TextStyle(color: AppColors.textMuted, fontSize: 11),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 16),
                      // Log out button
                      OutlinedButton.icon(
                        onPressed: _confirmLogout,
                        icon: const Icon(Icons.logout, size: 16, color: Colors.redAccent),
                        label: const Text(
                          "Log Out from MoodVerses",
                          style: TextStyle(color: Colors.redAccent, fontWeight: FontWeight.bold),
                        ),
                        style: OutlinedButton.styleFrom(
                          side: BorderSide(color: Colors.redAccent.withValues(alpha: 0.5)),
                          padding: const EdgeInsets.symmetric(vertical: 12),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        ),
                      ),
                    ] else ...[
                      // Guest Mode details
                      Container(
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: AppColors.emeraldSurfaceVariant,
                          borderRadius: BorderRadius.circular(12),
                          border: Border.all(color: AppColors.borderSubtle),
                        ),
                        child: const Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              "Guest Mode",
                              style: TextStyle(
                                color: AppColors.textPrimary,
                                fontSize: 13,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            SizedBox(height: 4),
                            Text(
                              "You are currently offline / local. Sign in with Google, Phone OTP, or Email to back up your chat history and favorites across devices.",
                              style: TextStyle(color: AppColors.textMuted, fontSize: 11.5),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(height: 14),
                      // Sign In / Create Account Button
                      ElevatedButton.icon(
                        onPressed: _openAuthDialog,
                        icon: const Icon(Icons.login, size: 18),
                        label: const Text(
                          "Sign In / Create Account",
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: AppColors.emeraldPrimary,
                          foregroundColor: AppColors.emeraldDeep,
                          padding: const EdgeInsets.symmetric(vertical: 12),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        ),
                      ),
                    ],

                    const SizedBox(height: 12),

                    // Close Button
                    TextButton(
                      onPressed: () => Navigator.of(context).pop(),
                      child: const Text("Done", style: TextStyle(color: AppColors.textMuted)),
                    ),
                  ],
                ),
        ),
      ),
    );
  }
}
