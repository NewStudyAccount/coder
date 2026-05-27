import 'package:flutter/material.dart';

class ProviderDemoPage extends StatelessWidget {
  const ProviderDemoPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('状态管理')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _SectionCard(
            title: 'setState',
            subtitle: '最基础的状态管理方式，适合简单局部状态',
            child: const _SetStateDemo(),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'InheritedWidget',
            subtitle: 'Flutter 内置的状态共享机制，Provider 底层原理',
            child: const _InheritedWidgetDemo(),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'ValueNotifier',
            subtitle: '轻量级可监听值，适合单一状态',
            child: const _ValueNotifierDemo(),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'StreamBuilder',
            subtitle: '基于 Stream 的异步状态管理',
            child: const _StreamBuilderDemo(),
          ),
        ],
      ),
    );
  }
}

class _SectionCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final Widget child;

  const _SectionCard({
    required this.title,
    required this.subtitle,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: Theme.of(context).colorScheme.primary,
                  ),
            ),
            const SizedBox(height: 4),
            Text(
              subtitle,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Theme.of(context).disabledColor,
                  ),
            ),
            const Divider(height: 24),
            child,
          ],
        ),
      ),
    );
  }
}

class _SetStateDemo extends StatefulWidget {
  const _SetStateDemo();

  @override
  State<_SetStateDemo> createState() => _SetStateDemoState();
}

class _SetStateDemoState extends State<_SetStateDemo> {
  int _counter = 0;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Text('计数: $_counter', style: const TextStyle(fontSize: 20)),
        const Spacer(),
        IconButton(
          onPressed: () => setState(() => _counter--),
          icon: const Icon(Icons.remove_circle),
        ),
        IconButton(
          onPressed: () => setState(() => _counter++),
          icon: const Icon(Icons.add_circle),
        ),
      ],
    );
  }
}

class _InheritedWidgetDemo extends StatefulWidget {
  const _InheritedWidgetDemo();

  @override
  State<_InheritedWidgetDemo> createState() => _InheritedWidgetDemoState();
}

class _InheritedWidgetDemoState extends State<_InheritedWidgetDemo> {
  int _sharedValue = 100;

  @override
  Widget build(BuildContext context) {
    return _SharedDataWidget(
      value: _sharedValue,
      child: Column(
        children: [
          Row(
            children: [
              const Text('共享值: '),
              _SharedDataConsumer(),
              const Spacer(),
              IconButton(
                onPressed: () => setState(() => _sharedValue++),
                icon: const Icon(Icons.add),
              ),
            ],
          ),
          const SizedBox(height: 8),
          const Text(
            'InheritedWidget 让子树中的任何 Widget 都能获取共享数据\n无需逐层传递回调',
            style: TextStyle(fontSize: 12, color: Colors.grey),
          ),
        ],
      ),
    );
  }
}

class _SharedDataWidget extends InheritedWidget {
  final int value;

  const _SharedDataWidget({
    required this.value,
    required super.child,
  });

  static _SharedDataWidget of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<_SharedDataWidget>()!;
  }

  @override
  bool updateShouldNotify(_SharedDataWidget oldWidget) {
    return value != oldWidget.value;
  }
}

class _SharedDataConsumer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final data = _SharedDataWidget.of(context);
    return Text(
      '${data.value}',
      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
    );
  }
}

class _ValueNotifierDemo extends StatefulWidget {
  const _ValueNotifierDemo();

  @override
  State<_ValueNotifierDemo> createState() => _ValueNotifierDemoState();
}

class _ValueNotifierDemoState extends State<_ValueNotifierDemo> {
  final _notifier = ValueNotifier<String>('Hello Flutter');

  @override
  void dispose() {
    _notifier.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ValueListenableBuilder<String>(
          valueListenable: _notifier,
          builder: (context, value, child) {
            return Text(
              value,
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            );
          },
        ),
        const SizedBox(height: 12),
        TextField(
          decoration: const InputDecoration(
            labelText: '输入文本',
            border: OutlineInputBorder(),
          ),
          onChanged: (v) => _notifier.value = v,
        ),
      ],
    );
  }
}

class _StreamBuilderDemo extends StatefulWidget {
  const _StreamBuilderDemo();

  @override
  State<_StreamBuilderDemo> createState() => _StreamBuilderDemoState();
}

class _StreamBuilderDemoState extends State<_StreamBuilderDemo> {
  int _streamValue = 0;

  Stream<int> _counterStream() async* {
    while (true) {
      await Future.delayed(const Duration(seconds: 1));
      yield _streamValue++;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        StreamBuilder<int>(
          stream: _counterStream(),
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              return Text(
                'Stream 值: ${snapshot.data}',
                style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              );
            }
            return const CircularProgressIndicator();
          },
        ),
        const SizedBox(height: 8),
        const Text(
          '每秒自动递增，StreamBuilder 自动重建',
          style: TextStyle(fontSize: 12, color: Colors.grey),
        ),
      ],
    );
  }
}
