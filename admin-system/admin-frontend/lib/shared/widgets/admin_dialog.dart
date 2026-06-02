import 'package:flutter/material.dart';

class AdminDialog {
  static Future<T?> showFormDialog<T>({
    required BuildContext context,
    required String title,
    required Widget form,
    required ValueChanged<void> onSave,
    double width = 600,
  }) {
    return showDialog<T>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(title),
        content: SizedBox(width: width, child: form),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(ctx).pop(),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () {
              onSave(null);
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  static Future<bool> showConfirmDialog({
    required BuildContext context,
    required String title,
    String content = '确认执行此操作？',
  }) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(title),
        content: Text(content),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(ctx).pop(false),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () => Navigator.of(ctx).pop(true),
            child: const Text('确定'),
          ),
        ],
      ),
    );
    return result ?? false;
  }
}
