import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../theme/app_theme.dart';

class DhikrItem {
  final String arabic;
  final String transliteration;
  final String meaning;
  final int defaultTarget;

  const DhikrItem({
    required this.arabic,
    required this.transliteration,
    required this.meaning,
    this.defaultTarget = 33,
  });
}

class TasbihScreen extends StatefulWidget {
  const TasbihScreen({super.key});

  @override
  State<TasbihScreen> createState() => _TasbihScreenState();
}

class _TasbihScreenState extends State<TasbihScreen> with SingleTickerProviderStateMixin {
  final List<DhikrItem> _dhikrPresets = const [
    DhikrItem(
      arabic: 'سُبْحَانَ اللَّهِ',
      transliteration: 'SubhanAllah',
      meaning: 'Glory be to Allah',
      defaultTarget: 33,
    ),
    DhikrItem(
      arabic: 'الْحَمْدُ لِلَّهِ',
      transliteration: 'Alhamdulillah',
      meaning: 'All praise is due to Allah',
      defaultTarget: 33,
    ),
    DhikrItem(
      arabic: 'اللَّهُ أَكْبَرُ',
      transliteration: 'Allahu Akbar',
      meaning: 'Allah is the Greatest',
      defaultTarget: 34,
    ),
    DhikrItem(
      arabic: 'أَسْتَغْفِرُ اللَّهَ',
      transliteration: 'Astaghfirullah',
      meaning: 'I seek forgiveness from Allah',
      defaultTarget: 100,
    ),
    DhikrItem(
      arabic: 'لَا إِلَهَ إِلَّا اللَّهُ',
      transliteration: 'La ilaha illallah',
      meaning: 'There is no god but Allah',
      defaultTarget: 100,
    ),
    DhikrItem(
      arabic: 'سُبْحَانَ اللَّهِ وَبِحَمْدِهِ',
      transliteration: 'SubhanAllahi wa bihamdihi',
      meaning: 'Glory and praise be to Allah',
      defaultTarget: 100,
    ),
    DhikrItem(
      arabic: 'اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ',
      transliteration: 'Salawat on Prophet (PBUH)',
      meaning: 'O Allah, send blessings upon Muhammad',
      defaultTarget: 100,
    ),
  ];

  int _selectedPresetIndex = 0;
  int _counter = 0;
  int _target = 33;
  int _completedCycles = 0;
  late AnimationController _pulseController;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _target = _dhikrPresets[0].defaultTarget;
    _pulseController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 120),
    );
    _scaleAnimation = Tween<double>(begin: 1.0, end: 0.95).animate(
      CurvedAnimation(parent: _pulseController, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _pulseController.dispose();
    super.dispose();
  }

  void _incrementCounter() {
    HapticFeedback.lightImpact();
    _pulseController.forward().then((_) => _pulseController.reverse());

    setState(() {
      _counter++;
      if (_counter >= _target) {
        _counter = 0;
        _completedCycles++;
        HapticFeedback.heavyImpact();
      }
    });
  }

  void _resetCounter() {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: AppTheme.cardBackground,
        title: const Text('Reset Tasbih Counter?', style: TextStyle(color: AppTheme.goldColor)),
        content: const Text(
          'This will reset your current count and cycle history for this session.',
          style: TextStyle(color: AppTheme.textSecondary),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('Cancel', style: TextStyle(color: AppTheme.textSecondary)),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: AppTheme.emeraldPrimary),
            onPressed: () {
              setState(() {
                _counter = 0;
                _completedCycles = 0;
              });
              Navigator.pop(ctx);
            },
            child: const Text('Reset', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  void _changePreset(int index) {
    setState(() {
      _selectedPresetIndex = index;
      _target = _dhikrPresets[index].defaultTarget;
      _counter = 0;
      _completedCycles = 0;
    });
  }

  @override
  Widget build(BuildContext context) {
    final activeDhikr = _dhikrPresets[_selectedPresetIndex];
    final double progress = _target > 0 ? (_counter / _target).clamp(0.0, 1.0) : 0.0;

    return Scaffold(
      backgroundColor: AppTheme.darkBackground,
      body: SafeArea(
        child: Column(
          children: [
            const SizedBox(height: 12),
            // Header
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        'Digital Tasbih',
                        style: TextStyle(
                          fontSize: 22,
                          fontWeight: FontWeight.bold,
                          color: AppTheme.goldColor,
                        ),
                      ),
                      Text(
                        'Cycles completed: $_completedCycles',
                        style: const TextStyle(
                          fontSize: 13,
                          color: AppTheme.textSecondary,
                        ),
                      ),
                    ],
                  ),
                  IconButton(
                    icon: const Icon(Icons.refresh, color: AppTheme.goldColor),
                    tooltip: 'Reset counter',
                    onPressed: _resetCounter,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Presets Horizontal Carousel
            SizedBox(
              height: 48,
              child: ListView.separated(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(horizontal: 16),
                itemCount: _dhikrPresets.length,
                separatorBuilder: (_, __) => const SizedBox(width: 8),
                itemBuilder: (context, i) {
                  final preset = _dhikrPresets[i];
                  final isSelected = i == _selectedPresetIndex;
                  return ChoiceChip(
                    label: Text(
                      preset.transliteration,
                      style: TextStyle(
                        color: isSelected ? Colors.black : AppTheme.textPrimary,
                        fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                        fontSize: 13,
                      ),
                    ),
                    selected: isSelected,
                    selectedColor: AppTheme.goldColor,
                    backgroundColor: AppTheme.cardBackground,
                    onSelected: (val) {
                      if (val) _changePreset(i);
                    },
                  );
                },
              ),
            ),

            const Spacer(),

            // Dhikr Display Info
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: Column(
                children: [
                  Text(
                    activeDhikr.arabic,
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 34,
                      fontWeight: FontWeight.bold,
                      color: AppTheme.goldColor,
                      fontFamily: 'Amiri',
                      height: 1.6,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    activeDhikr.transliteration,
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
                      color: AppTheme.textPrimary,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    activeDhikr.meaning,
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 13,
                      color: AppTheme.textSecondary,
                      fontStyle: FontStyle.italic,
                    ),
                  ),
                ],
              ),
            ),

            const Spacer(),

            // Interactive Bead / Counter Button
            ScaleTransition(
              scale: _scaleAnimation,
              child: GestureDetector(
                onTap: _incrementCounter,
                child: Stack(
                  alignment: Alignment.center,
                  children: [
                    // Outer Glow Ring
                    SizedBox(
                      width: 240,
                      height: 240,
                      child: CircularProgressIndicator(
                        value: progress,
                        strokeWidth: 8,
                        backgroundColor: AppTheme.cardBackground,
                        valueColor: const AlwaysStoppedAnimation<Color>(AppTheme.goldColor),
                      ),
                    ),
                    // Tap Sphere
                    Container(
                      width: 210,
                      height: 210,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        gradient: RadialGradient(
                          colors: [
                            AppTheme.emeraldLight.withValues(alpha: 0.9),
                            AppTheme.emeraldPrimary,
                            const Color(0xFF021B12),
                          ],
                          stops: const [0.2, 0.7, 1.0],
                        ),
                        boxShadow: [
                          BoxShadow(
                            color: AppTheme.goldColor.withValues(alpha: 0.25),
                            blurRadius: 30,
                            spreadRadius: 2,
                          ),
                        ],
                        border: Border.all(
                          color: AppTheme.goldColor.withValues(alpha: 0.6),
                          width: 2,
                        ),
                      ),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            '$_counter',
                            style: const TextStyle(
                              fontSize: 56,
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                              letterSpacing: 1.5,
                            ),
                          ),
                          Text(
                            '/ $_target',
                            style: TextStyle(
                              fontSize: 16,
                              color: AppTheme.goldColor.withValues(alpha: 0.8),
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            'TAP',
                            style: TextStyle(
                              fontSize: 11,
                              color: Colors.white.withValues(alpha: 0.5),
                              letterSpacing: 3,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),

            const Spacer(),

            // Quick Target Selection (33, 99, 100)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text('Target: ', style: TextStyle(color: AppTheme.textSecondary, fontSize: 13)),
                  const SizedBox(width: 8),
                  for (final t in [33, 99, 100])
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 4),
                      child: OutlinedButton(
                        style: OutlinedButton.styleFrom(
                          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                          side: BorderSide(
                            color: _target == t ? AppTheme.goldColor : Colors.white24,
                          ),
                          backgroundColor: _target == t ? AppTheme.goldColor.withValues(alpha: 0.15) : null,
                        ),
                        onPressed: () {
                          setState(() {
                            _target = t;
                            _counter = 0;
                          });
                        },
                        child: Text(
                          '$t',
                          style: TextStyle(
                            color: _target == t ? AppTheme.goldColor : AppTheme.textPrimary,
                            fontWeight: _target == t ? FontWeight.bold : FontWeight.normal,
                          ),
                        ),
                      ),
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
