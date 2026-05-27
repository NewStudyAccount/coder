import 'package:flutter/material.dart';

class WidgetCatalogPage extends StatelessWidget {
  const WidgetCatalogPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Widget 目录')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _SectionCard(
            title: 'Text & TextStyle',
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('普通文本', style: Theme.of(context).textTheme.bodyLarge),
                const SizedBox(height: 8),
                const Text(
                  '加粗斜体文本',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    fontStyle: FontStyle.italic,
                    fontSize: 18,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  '带颜色的文本',
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.primary,
                    fontSize: 16,
                    letterSpacing: 2,
                  ),
                ),
                const SizedBox(height: 8),
                const Text(
                  '带删除线的文本',
                  style: TextStyle(
                    decoration: TextDecoration.lineThrough,
                    decorationColor: Colors.red,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  '带阴影的文本',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    shadows: [
                      Shadow(
                        color: Colors.grey.withValues(alpha: 0.5),
                        offset: const Offset(2, 2),
                        blurRadius: 4,
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Button 系列',
            child: Wrap(
              spacing: 12,
              runSpacing: 12,
              children: [
                FilledButton(
                  onPressed: () {},
                  child: const Text('FilledButton'),
                ),
                ElevatedButton(
                  onPressed: () {},
                  child: const Text('ElevatedButton'),
                ),
                OutlinedButton(
                  onPressed: () {},
                  child: const Text('OutlinedButton'),
                ),
                TextButton(
                  onPressed: () {},
                  child: const Text('TextButton'),
                ),
                IconButton(
                  onPressed: () {},
                  icon: const Icon(Icons.favorite),
                  tooltip: 'IconButton',
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'TextField 输入框',
            child: Column(
              children: [
                TextField(
                  decoration: InputDecoration(
                    labelText: '用户名',
                    prefixIcon: const Icon(Icons.person),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                ),
                const SizedBox(height: 12),
                TextField(
                  obscureText: true,
                  decoration: InputDecoration(
                    labelText: '密码',
                    prefixIcon: const Icon(Icons.lock),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Chip 标签',
            child: Wrap(
              spacing: 8,
              runSpacing: 8,
              children: [
                Chip(
                  label: const Text('Flutter'),
                  avatar: const Icon(Icons.flutter_dash, size: 18),
                  onDeleted: () {},
                ),
                const Chip(label: Text('Dart')),
                FilterChip(
                  label: const Text('可选标签'),
                  onSelected: (selected) {},
                ),
                ActionChip(
                  label: const Text('操作标签'),
                  onPressed: () {},
                ),
                InputChip(
                  label: const Text('输入标签'),
                  onDeleted: () {},
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'ProgressIndicator 进度',
            child: Column(
              children: [
                LinearProgressIndicator(
                  value: 0.7,
                  borderRadius: BorderRadius.circular(4),
                ),
                const SizedBox(height: 16),
                const CircularProgressIndicator(),
                const SizedBox(height: 16),
                CircularProgressIndicator(
                  value: 0.5,
                  strokeWidth: 6,
                  backgroundColor: Colors.grey.shade200,
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Switch / Checkbox / Radio',
            child: _SwitchCheckboxDemo(),
          ),
          const SizedBox(height: 16),
          _SectionCard(
            title: 'Slider / RangeSlider',
            child: _SliderDemo(),
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

class _SwitchCheckboxDemo extends StatefulWidget {
  @override
  State<_SwitchCheckboxDemo> createState() => _SwitchCheckboxDemoState();
}

class _SwitchCheckboxDemoState extends State<_SwitchCheckboxDemo> {
  bool _switchValue = true;
  bool _checkboxValue = false;
  String _radioValue = 'A';

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SwitchListTile(
          title: const Text('开关'),
          value: _switchValue,
          onChanged: (v) => setState(() => _switchValue = v),
        ),
        CheckboxListTile(
          title: const Text('复选框'),
          value: _checkboxValue,
          onChanged: (v) => setState(() => _checkboxValue = v ?? false),
        ),
        RadioGroup<String>(
          groupValue: _radioValue,
          onChanged: (v) => setState(() => _radioValue = v ?? 'A'),
          child: Column(
            children: [
              RadioListTile<String>(
                title: const Text('选项 A'),
                value: 'A',
              ),
              RadioListTile<String>(
                title: const Text('选项 B'),
                value: 'B',
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class _SliderDemo extends StatefulWidget {
  @override
  State<_SliderDemo> createState() => _SliderDemoState();
}

class _SliderDemoState extends State<_SliderDemo> {
  double _sliderValue = 50;
  RangeValues _rangeValues = const RangeValues(25, 75);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('当前值: ${_sliderValue.toStringAsFixed(0)}'),
        Slider(
          value: _sliderValue,
          min: 0,
          max: 100,
          divisions: 100,
          label: _sliderValue.toStringAsFixed(0),
          onChanged: (v) => setState(() => _sliderValue = v),
        ),
        const SizedBox(height: 8),
        Text('范围: ${_rangeValues.start.toStringAsFixed(0)} - ${_rangeValues.end.toStringAsFixed(0)}'),
        RangeSlider(
          values: _rangeValues,
          min: 0,
          max: 100,
          divisions: 100,
          labels: RangeLabels(
            _rangeValues.start.toStringAsFixed(0),
            _rangeValues.end.toStringAsFixed(0),
          ),
          onChanged: (v) => setState(() => _rangeValues = v),
        ),
      ],
    );
  }
}
