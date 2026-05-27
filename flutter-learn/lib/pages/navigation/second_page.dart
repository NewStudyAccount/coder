import 'package:flutter/material.dart';

class SecondPage extends StatelessWidget {
  final String title;
  final String message;

  const SecondPage({
    super.key,
    this.title = '第二页',
    this.message = '这是第二页',
  });

  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)?.settings.arguments as Map<String, dynamic>?;
    final displayTitle = args?['title'] as String? ?? title;
    final displayMessage = args?['message'] as String? ?? message;

    return Scaffold(
      appBar: AppBar(title: Text(displayTitle)),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.navigation, size: 64, color: Colors.blue),
              const SizedBox(height: 24),
              Text(
                displayMessage,
                style: Theme.of(context).textTheme.headlineSmall,
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 32),
              FilledButton.icon(
                onPressed: () => Navigator.pop(context),
                icon: const Icon(Icons.arrow_back),
                label: const Text('返回上一页'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
