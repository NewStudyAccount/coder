import 'package:flutter/material.dart';

class ThirdPage extends StatefulWidget {
  const ThirdPage({super.key});

  @override
  State<ThirdPage> createState() => _ThirdPageState();
}

class _ThirdPageState extends State<ThirdPage> {
  String _selectedValue = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('返回值演示')),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.back_hand, size: 64, color: Colors.purple),
              const SizedBox(height: 24),
              const Text(
                '选择一个值返回给上一页',
                style: TextStyle(fontSize: 18),
              ),
              const SizedBox(height: 24),
              Wrap(
                spacing: 12,
                runSpacing: 12,
                children: ['🍎 苹果', '🍌 香蕉', '🍇 葡萄', '🍊 橙子', '🍓 草莓']
                    .map((item) => ChoiceChip(
                          label: Text(item),
                          selected: _selectedValue == item,
                          onSelected: (_) {
                            setState(() => _selectedValue = item);
                          },
                        ))
                    .toList(),
              ),
              const SizedBox(height: 32),
              FilledButton.icon(
                onPressed: _selectedValue.isEmpty
                    ? null
                    : () => Navigator.pop(context, _selectedValue),
                icon: const Icon(Icons.check),
                label: const Text('确认返回'),
              ),
              const SizedBox(height: 12),
              OutlinedButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('取消'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
