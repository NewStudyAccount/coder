import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:admin/features/auth/presentation/providers/auth_provider.dart';

class PermissionWidget extends ConsumerWidget {
  final String permission;
  final Widget child;
  final Widget? fallback;

  const PermissionWidget({
    super.key,
    required this.permission,
    required this.child,
    this.fallback,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    if (authState is AuthAuthenticated) {
      if (authState.permissions.contains('*:*:*') ||
          authState.permissions.contains(permission)) {
        return child;
      }
    }
    return fallback ?? const SizedBox.shrink();
  }
}

class PermissionButton extends ConsumerWidget {
  final String permission;
  final String label;
  final IconData? icon;
  final VoidCallback onPressed;
  final ButtonStyle? style;
  final bool isDestructive;

  const PermissionButton({
    super.key,
    required this.permission,
    required this.label,
    this.icon,
    required this.onPressed,
    this.style,
    this.isDestructive = false,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    if (authState is AuthAuthenticated) {
      if (authState.permissions.contains('*:*:*') ||
          authState.permissions.contains(permission)) {
        if (icon != null) {
          return FilledButton.icon(
            onPressed: onPressed,
            icon: Icon(icon),
            label: Text(label),
            style: style ??
                (isDestructive
                    ? FilledButton.styleFrom(
                        backgroundColor: Theme.of(context).colorScheme.error)
                    : null),
          );
        }
        return FilledButton(
          onPressed: onPressed,
          style: style ??
              (isDestructive
                  ? FilledButton.styleFrom(
                      backgroundColor: Theme.of(context).colorScheme.error)
                  : null),
          child: Text(label),
        );
      }
    }
    return const SizedBox.shrink();
  }
}
