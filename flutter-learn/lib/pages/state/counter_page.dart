import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../app/state/counter_provider.dart';

class CounterPage extends StatelessWidget {
  const CounterPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('计数器 - Provider')),
      body: Center(
        child: Consumer<CounterProvider>(
          builder: (context, counter, child) {
            return Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  '当前计数值:',
                  style: TextStyle(fontSize: 18),
                ),
                const SizedBox(height: 16),
                Text(
                  '${counter.count}',
                  style: TextStyle(
                    fontSize: 72,
                    fontWeight: FontWeight.bold,
                    color: Theme.of(context).colorScheme.primary,
                  ),
                ),
                const SizedBox(height: 32),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    FloatingActionButton(
                      heroTag: 'decrement',
                      onPressed: counter.decrement,
                      child: const Icon(Icons.remove),
                    ),
                    const SizedBox(width: 24),
                    FloatingActionButton(
                      heroTag: 'reset',
                      onPressed: counter.reset,
                      child: const Icon(Icons.refresh),
                    ),
                    const SizedBox(width: 24),
                    FloatingActionButton(
                      heroTag: 'increment',
                      onPressed: counter.increment,
                      child: const Icon(Icons.add),
                    ),
                  ],
                ),
                const SizedBox(height: 48),
                Card(
                  margin: const EdgeInsets.symmetric(horizontal: 32),
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Provider 使用说明:',
                          style: Theme.of(context).textTheme.titleSmall?.copyWith(
                                fontWeight: FontWeight.bold,
                              ),
                        ),
                        const SizedBox(height: 8),
                        const Text('1. 创建 CounterProvider 继承 ChangeNotifier'),
                        const Text('2. 在 main.dart 用 ChangeNotifierProvider 注册'),
                        const Text('3. 用 Consumer<CounterProvider> 获取状态'),
                        const Text('4. 调用方法后 notifyListeners() 通知更新'),
                      ],
                    ),
                  ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }
}
