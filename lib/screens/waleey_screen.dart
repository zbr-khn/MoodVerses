import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import '../models/chat_message.dart';
import '../services/auth_service.dart';
import '../services/chat_repository.dart';
import '../services/waleey_ai_service.dart';
import '../theme/app_theme.dart';
import '../components/user_profile_dialog.dart';

class WaleeyScreen extends StatefulWidget {
  const WaleeyScreen({super.key});

  @override
  State<WaleeyScreen> createState() => _WaleeyScreenState();
}

class _WaleeyScreenState extends State<WaleeyScreen> {
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  final TextEditingController _inputController = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  List<ChatSession> _sessions = [];
  String _activeSessionId = "";
  bool _isThinking = false;
  String? _userName;

  ChatSession get _activeSession {
    return _sessions.firstWhere(
      (s) => s.id == _activeSessionId,
      orElse: () => _sessions.isNotEmpty ? _sessions.first : ChatSession(),
    );
  }

  @override
  void initState() {
    super.initState();
    _loadUserName();
    _loadInitialSessions();
  }

  void _loadUserName() async {
    final name = await AuthService.getUserName();
    if (mounted) {
      setState(() => _userName = name);
    }
  }

  @override
  void dispose() {
    _inputController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _loadInitialSessions() async {
    final localSessions = await ChatRepository.loadSessions();
    setState(() {
      _sessions = localSessions;
      _activeSessionId = localSessions.first.id;
    });

    // If logged in, fetch and merge cloud sessions
    final user = AuthService.currentUser;
    if (user != null) {
      final cloudSessions = await ChatRepository.fetchCloudSessions(user.uid);
      if (cloudSessions.isNotEmpty) {
        for (final s in cloudSessions) {
          await ChatRepository.saveSingleSession(s);
        }
        final refreshed = await ChatRepository.loadSessions();
        setState(() {
          _sessions = refreshed;
          if (!_sessions.any((s) => s.id == _activeSessionId)) {
            _activeSessionId = _sessions.first.id;
          }
        });
      }
    }
  }

  void _createNewChat() async {
    final newSession = ChatSession();
    await ChatRepository.saveSingleSession(newSession);
    final refreshed = await ChatRepository.loadSessions();
    setState(() {
      _sessions = refreshed;
      _activeSessionId = newSession.id;
      _inputController.clear();
    });
    if (_scaffoldKey.currentState?.isDrawerOpen ?? false) {
      Navigator.pop(context);
    }
  }

  void _sendMessage(String text) async {
    final query = WaleeyAiService.sanitizeInput(text);
    if (query.isEmpty || _isThinking) return;

    final userMsg = ChatMessage(text: query, isUser: true);
    final updatedMessages = [..._activeSession.messages, userMsg];

    final newTitle = _activeSession.title == "New Conversation"
        ? (query.length > 25 ? "${query.substring(0, 25)}..." : query)
        : _activeSession.title;

    final intermediateSession = _activeSession.copyWith(
      title: newTitle,
      messages: updatedMessages,
      updatedAt: DateTime.now().millisecondsSinceEpoch,
    );

    await ChatRepository.saveSingleSession(intermediateSession);
    final loaded = await ChatRepository.loadSessions();
    setState(() {
      _sessions = loaded;
      _inputController.clear();
      _isThinking = true;
    });

    _scrollToBottom();

    // Live AI query with conversation history & personalized greeting
    final aiResponse = await WaleeyAiService.processQuery(
      query,
      history: updatedMessages,
      userName: _userName,
    );
    final finalMessages = [...updatedMessages, aiResponse];
    final finalSession = intermediateSession.copyWith(
      messages: finalMessages,
      updatedAt: DateTime.now().millisecondsSinceEpoch,
    );

    await ChatRepository.saveSingleSession(finalSession);
    final finalLoaded = await ChatRepository.loadSessions();

    setState(() {
      _sessions = finalLoaded;
      _isThinking = false;
    });

    _scrollToBottom();

    // Sync to Cloud Firestore if logged in
    final user = AuthService.currentUser;
    if (user != null) {
      await ChatRepository.syncSessionToCloud(user.uid, finalSession);
    }
  }

  void _openAccountDialog() {
    UserProfileDialog.show(
      context,
      onProfileUpdated: () {
        _loadUserName();
        _loadInitialSessions();
        setState(() {});
      },
    );
  }

  void _scrollToBottom() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final isLoggedIn = AuthService.isLoggedIn;
    final currentUser = AuthService.currentUser;

    return Scaffold(
      key: _scaffoldKey,
      backgroundColor: AppColors.emeraldDeep,
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.menu, color: AppColors.goldAccent),
          onPressed: () => _scaffoldKey.currentState?.openDrawer(),
        ),
        title: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(6),
              decoration: BoxDecoration(
                color: AppColors.emeraldSurfaceVariant,
                shape: BoxShape.circle,
                border: Border.all(color: AppColors.goldAccent),
              ),
              child: const Icon(Icons.auto_awesome, color: AppColors.goldAccent, size: 18),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Row(
                    children: [
                      Text("Waleey", style: TextStyle(color: AppColors.textPrimary, fontSize: 17, fontWeight: FontWeight.bold)),
                      SizedBox(width: 6),
                      Text("(وَلِيّ)", style: TextStyle(color: AppColors.goldLight, fontSize: 13)),
                    ],
                  ),
                  Text(
                    _userName != null && _userName!.isNotEmpty
                        ? "Assalamu Alaikum, $_userName"
                        : "Your Islamic AI Companion",
                    style: const TextStyle(color: AppColors.textMuted, fontSize: 10.5),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
          ],
        ),
        actions: [
          IconButton(
            onPressed: _openAccountDialog,
            icon: Stack(
              clipBehavior: Clip.none,
              children: [
                const Icon(Icons.account_circle, color: AppColors.goldLight, size: 24),
                if (isLoggedIn)
                  Positioned(
                    right: -1,
                    top: -1,
                    child: Container(
                      width: 8,
                      height: 8,
                      decoration: const BoxDecoration(
                        color: AppColors.emeraldPrimary,
                        shape: BoxShape.circle,
                      ),
                    ),
                  ),
              ],
            ),
            tooltip: "User Profile",
          ),
          IconButton(
            onPressed: _createNewChat,
            icon: const Icon(Icons.add_comment, color: AppColors.goldLight),
            tooltip: "New Chat",
          ),
        ],
      ),
      drawer: _buildDrawer(isLoggedIn, currentUser),
      body: Column(
        children: [
          // Quick Prompts Row
          SizedBox(
            height: 44,
            child: ListView.separated(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
              itemCount: WaleeyAiService.starterPrompts.length,
              separatorBuilder: (_, __) => const SizedBox(width: 8),
              itemBuilder: (context, index) {
                final prompt = WaleeyAiService.starterPrompts[index];
                return InkWell(
                  onTap: () => _sendMessage(prompt),
                  borderRadius: BorderRadius.circular(20),
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: AppColors.emeraldSurface,
                      borderRadius: BorderRadius.circular(20),
                      border: Border.all(color: AppColors.borderSubtle),
                    ),
                    child: Text(
                      prompt,
                      style: const TextStyle(color: AppColors.textSecondary, fontSize: 11.5),
                    ),
                  ),
                );
              },
            ),
          ),

          // Messages List / Welcoming Greeting
          Expanded(
            child: _activeSession.messages.isEmpty && !_isThinking
                ? Center(
                    child: SingleChildScrollView(
                      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
                      child: Container(
                        padding: const EdgeInsets.all(22),
                        decoration: BoxDecoration(
                          color: AppColors.emeraldSurface,
                          borderRadius: BorderRadius.circular(20),
                          border: Border.all(color: AppColors.borderSubtle),
                        ),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Container(
                              padding: const EdgeInsets.all(12),
                              decoration: const BoxDecoration(
                                color: AppColors.emeraldSurfaceVariant,
                                shape: BoxShape.circle,
                              ),
                              child: const Icon(Icons.auto_awesome, color: AppColors.goldAccent, size: 30),
                            ),
                            const SizedBox(height: 12),
                            Text(
                              _userName != null && _userName!.isNotEmpty
                                  ? "Assalamu Alaikum, $_userName! 🌸"
                                  : "Assalamu Alaikum! 🌸",
                              style: const TextStyle(
                                color: AppColors.goldLight,
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                              ),
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 8),
                            const Text(
                              "I am Waleey (وَلِيّ), your Islamic AI companion. Ask me any inquiry regarding the Quran, Hadith, prayers, or Islamic history.",
                              style: TextStyle(color: AppColors.textSecondary, fontSize: 13, height: 1.4),
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 16),
                            Wrap(
                              spacing: 8,
                              runSpacing: 8,
                              alignment: WrapAlignment.center,
                              children: [
                                _buildQuickChip("Attaining Khushu in Salah"),
                                _buildQuickChip("Virtues of Tahajjud"),
                                _buildQuickChip("Conditions of accepted Dua"),
                                _buildQuickChip("Overcoming anxiety & grief"),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                  )
                : ListView.builder(
                    controller: _scrollController,
                    padding: const EdgeInsets.all(16),
                    itemCount: _activeSession.messages.length + (_isThinking ? 1 : 0),
                    itemBuilder: (context, index) {
                      if (index == _activeSession.messages.length && _isThinking) {
                        return Container(
                          margin: const EdgeInsets.only(top: 8),
                          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                          decoration: BoxDecoration(
                            color: AppColors.emeraldSurface,
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: const Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              SizedBox(
                                width: 16,
                                height: 16,
                                child: CircularProgressIndicator(strokeWidth: 2, color: AppColors.goldAccent),
                              ),
                              SizedBox(width: 10),
                              Text(
                                "Waleey is reflecting upon Islamic knowledge...",
                                style: TextStyle(color: AppColors.textSecondary, fontSize: 12),
                              ),
                            ],
                          ),
                        );
                      }

                      final msg = _activeSession.messages[index];
                      return _buildMessageItem(msg);
                    },
                  ),
          ),

          // Input Bar
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            decoration: const BoxDecoration(
              color: AppColors.emeraldSurface,
              border: Border(top: BorderSide(color: AppColors.borderSubtle, width: 0.5)),
            ),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _inputController,
                    maxLines: 3,
                    minLines: 1,
                    style: const TextStyle(color: AppColors.textPrimary, fontSize: 13.5),
                    decoration: const InputDecoration(
                      hintText: "Ask about Seerah, Salah, History, Ayats...",
                      hintStyle: TextStyle(color: Colors.grey, fontSize: 12.5),
                      border: InputBorder.none,
                      contentPadding: EdgeInsets.symmetric(horizontal: 8, vertical: 8),
                    ),
                  ),
                ),
                IconButton(
                  onPressed: () => _sendMessage(_inputController.text),
                  icon: const Icon(Icons.send, color: AppColors.goldAccent),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDrawer(bool isLoggedIn, user) {
    return Drawer(
      backgroundColor: AppColors.emeraldSurface,
      child: SafeArea(
        child: Column(
          children: [
            // Drawer Header
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text("Chat History", style: TextStyle(color: AppColors.textPrimary, fontSize: 18, fontWeight: FontWeight.bold)),
                      Text("سجل المحادثات", style: TextStyle(color: AppColors.goldLight, fontSize: 11)),
                    ],
                  ),
                  IconButton(
                    onPressed: () => Navigator.pop(context),
                    icon: const Icon(Icons.close, color: AppColors.textMuted),
                  ),
                ],
              ),
            ),

            // New Conversation Button
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: SizedBox(
                width: double.infinity,
                child: OutlinedButton.icon(
                  onPressed: _createNewChat,
                  style: OutlinedButton.styleFrom(
                    backgroundColor: AppColors.emeraldSurfaceVariant,
                    side: const BorderSide(color: AppColors.goldAccent),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                  icon: const Icon(Icons.add, color: AppColors.goldAccent),
                  label: const Text("New Conversation", style: TextStyle(color: AppColors.textPrimary, fontWeight: FontWeight.bold)),
                ),
              ),
            ),
            const SizedBox(height: 12),
            const Divider(color: AppColors.borderSubtle),

            // Sessions List
            Expanded(
              child: ListView.builder(
                padding: const EdgeInsets.symmetric(horizontal: 12),
                itemCount: _sessions.length,
                itemBuilder: (context, index) {
                  final session = _sessions[index];
                  final isSelected = session.id == _activeSessionId;

                  return Container(
                    margin: const EdgeInsets.only(bottom: 6),
                    decoration: BoxDecoration(
                      color: isSelected ? AppColors.emeraldSurfaceVariant : Colors.transparent,
                      borderRadius: BorderRadius.circular(12),
                      border: isSelected ? Border.all(color: AppColors.borderSubtle) : null,
                    ),
                    child: ListTile(
                      dense: true,
                      leading: Icon(Icons.chat_bubble_outline, color: isSelected ? AppColors.goldAccent : AppColors.textMuted, size: 18),
                      title: Text(
                        session.title,
                        style: TextStyle(
                          color: isSelected ? AppColors.goldLight : AppColors.textPrimary,
                          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                          fontSize: 13,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                      trailing: _sessions.length > 1
                          ? IconButton(
                              icon: const Icon(Icons.delete_outline, size: 16, color: AppColors.textMuted),
                              onPressed: () async {
                                final remaining = await ChatRepository.deleteSession(session.id);
                                if (isLoggedIn && user != null) {
                                  await ChatRepository.deleteCloudSession(user.uid, session.id);
                                }
                                setState(() {
                                  _sessions = remaining;
                                  if (_activeSessionId == session.id) {
                                    _activeSessionId = remaining.first.id;
                                  }
                                });
                              },
                            )
                          : null,
                      onTap: () {
                        setState(() => _activeSessionId = session.id);
                        Navigator.pop(context);
                      },
                    ),
                  );
                },
              ),
            ),

            // Account / User Profile Footer
            InkWell(
              onTap: () {
                Navigator.pop(context);
                _openAccountDialog();
              },
              borderRadius: BorderRadius.circular(14),
              child: Container(
                margin: const EdgeInsets.all(12),
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColors.emeraldSurfaceVariant,
                  borderRadius: BorderRadius.circular(14),
                  border: Border.all(color: AppColors.borderSubtle),
                ),
                child: Row(
                  children: [
                    Icon(
                      isLoggedIn ? Icons.cloud_done : Icons.account_circle,
                      color: isLoggedIn ? AppColors.emeraldPrimary : AppColors.goldLight,
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            _userName != null && _userName!.isNotEmpty
                                ? _userName!
                                : (isLoggedIn ? (user?.phoneNumber ?? user?.email ?? "Cloud User") : "Guest Account"),
                            style: const TextStyle(color: AppColors.textPrimary, fontSize: 12.5, fontWeight: FontWeight.bold),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                          Text(
                            isLoggedIn ? "Cloud Sync Active • View profile" : "Local Storage • Tap to view profile",
                            style: TextStyle(color: isLoggedIn ? AppColors.emeraldPrimary : AppColors.textMuted, fontSize: 10.5),
                          ),
                        ],
                      ),
                    ),
                    const Icon(Icons.chevron_right, color: AppColors.textMuted, size: 18),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMessageItem(ChatMessage msg) {
    if (msg.isUser) {
      return Container(
        margin: const EdgeInsets.only(bottom: 12, left: 40),
        alignment: Alignment.centerRight,
        child: Container(
          padding: const EdgeInsets.all(14),
          decoration: BoxDecoration(
            color: AppColors.emeraldSurfaceVariant,
            borderRadius: const BorderRadius.only(
              topLeft: Radius.circular(16),
              topRight: Radius.circular(16),
              bottomLeft: Radius.circular(16),
              bottomRight: Radius.circular(4),
            ),
            border: Border.all(color: AppColors.borderSubtle),
          ),
          child: Text(
            msg.text,
            style: const TextStyle(color: AppColors.textPrimary, fontSize: 14),
          ),
        ),
      );
    }

    return Container(
      margin: const EdgeInsets.only(bottom: 16, right: 24),
      alignment: Alignment.centerLeft,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: AppColors.emeraldSurface,
              borderRadius: const BorderRadius.only(
                topLeft: Radius.circular(16),
                topRight: Radius.circular(16),
                bottomRight: Radius.circular(16),
                bottomLeft: Radius.circular(4),
              ),
              border: Border.all(color: AppColors.borderSubtle),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                MarkdownBody(
                  data: msg.text,
                  styleSheet: MarkdownStyleSheet(
                    p: const TextStyle(color: AppColors.textPrimary, fontSize: 13.5, height: 1.5),
                    strong: const TextStyle(color: AppColors.goldLight, fontWeight: FontWeight.bold),
                    em: const TextStyle(color: AppColors.textSecondary, fontStyle: FontStyle.italic),
                  ),
                ),

                // Verified Citations
                if (msg.citations.isNotEmpty) ...[
                  const SizedBox(height: 12),
                  const Divider(color: AppColors.borderSubtle, thickness: 0.5),
                  const SizedBox(height: 8),
                  const Row(
                    children: [
                      Icon(Icons.menu_book, color: AppColors.goldAccent, size: 13),
                      SizedBox(width: 6),
                      Text("Verified References:", style: TextStyle(color: AppColors.goldLight, fontSize: 11, fontWeight: FontWeight.bold)),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Wrap(
                    spacing: 6,
                    runSpacing: 6,
                    children: msg.citations.map((c) => Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                      decoration: BoxDecoration(
                        color: AppColors.emeraldSurfaceVariant.withValues(alpha: 0.7),
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: AppColors.borderSubtle),
                      ),
                      child: Text(
                        c,
                        style: const TextStyle(color: AppColors.textSecondary, fontSize: 11, fontWeight: FontWeight.w500),
                      ),
                    )).toList(),
                  ),
                ],
              ],
            ),
          ),

          // Related Topics Chips
          if (msg.suggestions.isNotEmpty) ...[
            const SizedBox(height: 6),
            Wrap(
              spacing: 6,
              children: msg.suggestions.take(2).map((s) {
                return ActionChip(
                  label: Text("✦ $s", style: const TextStyle(color: AppColors.textSecondary, fontSize: 11)),
                  backgroundColor: AppColors.emeraldSurfaceVariant,
                  side: const BorderSide(color: AppColors.borderSubtle),
                  onPressed: () => _sendMessage(s),
                );
              }).toList(),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildQuickChip(String text) {
    return InkWell(
      onTap: () => _sendMessage(text),
      borderRadius: BorderRadius.circular(20),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
        decoration: BoxDecoration(
          color: AppColors.emeraldSurfaceVariant,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: AppColors.borderSubtle),
        ),
        child: Text(
          text,
          style: const TextStyle(color: AppColors.goldLight, fontSize: 11.5, fontWeight: FontWeight.w500),
        ),
      ),
    );
  }
}
