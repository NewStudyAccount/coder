import 'package:flutter/material.dart';

class LayoutDemoPage extends StatelessWidget {
  const LayoutDemoPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('布局系统')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _SectionCard(
            title: 'Row 水平布局',
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('MainAxisAlignment.start'),
                const SizedBox(height: 8),
                Container(
                  height: 60,
                  color: Colors.blue.withValues(alpha: 0.1),
                  child: const Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      _ColorBox(Colors.red, '1'),
                      _ColorBox(Colors.green, '2'),
                      _ColorBox(Colors.blue, '3'),
                    ],
                  ),
                ),
                const SizedBox(height: 12),
                const Text('MainAxisAlignment.spaceEvenly'),
                const SizedBox(height: 8),
                Container(
                  height: 60,
                  color: Colors.blue.withValues(alpha: 0.1),
                  child: const Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      _ColorBox(Colors.red, '1'),
                      _ColorBox(Colors.green, '2'),
                      _ColorBox(Colors.blue, '3'),
                    ],
                  ),
                ),
                const SizedBox(height: 12),
                const Text('MainAxisAlignment.spaceBetween'),
                const SizedBox(height: 8),
                Container(
                  height: 60,
                  color: Colors.blue.withValues(alpha: 0.1),
                  child: const Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      _ColorBox(Colors.red, '1'),
                      _ColorBox(Colors.green, '2'),
                      _ColorBox(Colors.blue, '3'),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Column 垂直布局',
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('CrossAxisAlignment.start'),
                      const SizedBox(height: 8),
                      Container(
                        width: double.infinity,
                        color: Colors.green.withValues(alpha: 0.1),
                        child: const Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            _ColorBox(Colors.red, '1', width: 60),
                            _ColorBox(Colors.green, '2', width: 80),
                            _ColorBox(Colors.blue, '3', width: 50),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('CrossAxisAlignment.end'),
                      const SizedBox(height: 8),
                      Container(
                        width: double.infinity,
                        color: Colors.green.withValues(alpha: 0.1),
                        child: const Column(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            _ColorBox(Colors.red, '1', width: 60),
                            _ColorBox(Colors.green, '2', width: 80),
                            _ColorBox(Colors.blue, '3', width: 50),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Stack 层叠布局',
            child: SizedBox(
              height: 200,
              child: Stack(
                children: [
                  Container(
                    width: double.infinity,
                    height: 200,
                    decoration: BoxDecoration(
                      color: Colors.indigo.withValues(alpha: 0.2),
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  Positioned(
                    top: 16,
                    left: 16,
                    child: Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.red.withValues(alpha: 0.8),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: const Text(
                        '左上角',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                  Positioned(
                    bottom: 16,
                    right: 16,
                    child: Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Colors.green.withValues(alpha: 0.8),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: const Text(
                        '右下角',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ),
                  const Center(
                    child: Text('居中', style: TextStyle(fontSize: 20)),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Wrap 流式布局',
            child: Wrap(
              spacing: 8,
              runSpacing: 8,
              children: List.generate(
                20,
                (i) => Chip(
                  label: Text('标签 ${i + 1}'),
                  backgroundColor: Colors
                      .primaries[i % Colors.primaries.length]
                      .withValues(alpha: 0.2),
                ),
              ),
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Expanded / Flexible',
            child: Column(
              children: [
                const Text('Expanded 按比例分配空间:'),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Expanded(
                      flex: 1,
                      child: Container(
                        height: 50,
                        color: Colors.red.withValues(alpha: 0.5),
                        child: const Center(child: Text('flex: 1')),
                      ),
                    ),
                    Expanded(
                      flex: 2,
                      child: Container(
                        height: 50,
                        color: Colors.green.withValues(alpha: 0.5),
                        child: const Center(child: Text('flex: 2')),
                      ),
                    ),
                    Expanded(
                      flex: 1,
                      child: Container(
                        height: 50,
                        color: Colors.blue.withValues(alpha: 0.5),
                        child: const Center(child: Text('flex: 1')),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                const Text('Flexible 可收缩:'),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Flexible(
                      child: Container(
                        height: 50,
                        color: Colors.orange.withValues(alpha: 0.5),
                        child: const Center(child: Text('Flexible')),
                      ),
                    ),
                    Container(
                      width: 100,
                      height: 50,
                      color: Colors.purple.withValues(alpha: 0.5),
                      child: const Center(child: Text('固定100')),
                    ),
                  ],
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'GridView 网格布局',
            child: SizedBox(
              height: 200,
              child: GridView.count(
                crossAxisCount: 3,
                mainAxisSpacing: 8,
                crossAxisSpacing: 8,
                children: List.generate(
                  6,
                  (i) => Container(
                    decoration: BoxDecoration(
                      color: Colors.primaries[i % Colors.primaries.length]
                          .withValues(alpha: 0.3),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Center(child: Text('Item ${i + 1}')),
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _SectionCard extends StatelessWidget {
  final String title;
  final Widget child;

  const _SectionCard({required this.title, required this.child});

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
            const Divider(height: 24),
            child,
          ],
        ),
      ),
    );
  }
}

class _ColorBox extends StatelessWidget {
  final Color color;
  final String label;
  final double? width;

  const _ColorBox(this.color, this.label, {this.width});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width ?? 50,
      height: 40,
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.7),
        borderRadius: BorderRadius.circular(6),
      ),
      child: Center(
        child: Text(
          label,
          style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
        ),
      ),
    );
  }
}
