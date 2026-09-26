import 'package:flutter/material.dart';
import '../core/theme/app_theme.dart';

class NutriCard extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;

  const NutriCard({super.key, required this.child, this.padding});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: padding ?? const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surface,
        borderRadius: BorderRadius.circular(24),
        border: Border.all(
          color: Theme.of(context).brightness == Brightness.dark
              ? const Color(0xFF26362F)
              : const Color(0xFFE2E8F0),
          width: 1,
        ),
      ),
      child: child,
    );
  }
}
