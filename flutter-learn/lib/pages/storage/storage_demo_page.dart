import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class StorageDemoPage extends StatefulWidget {
  const StorageDemoPage({super.key});

  @override
  State<StorageDemoPage> createState() => _StorageDemoPageState();
}

class _StorageDemoPageState extends State<StorageDemoPage> {
  final _keyController = TextEditingController();
  final _valueController = TextEditingController();
  SharedPreferences? _prefs;
  Map<String, String> _allData = {};
  String _statusMessage = '';

  @override
  void initState() {
    super.initState();
    _initPrefs();
  }

  Future<void> _initPrefs() async {
    _prefs = await SharedPreferences.getInstance();
    _loadAllData();
  }

  Future<void> _loadAllData() async {
    if (_prefs == null) return;
    final keys = _prefs!.getKeys();
    final data = <String, String>{};
    for (final key in keys) {
      data[key] = _prefs!.get(key).toString();
    }
    setState(() {
      _allData = data;
    });
  }

  Future<void> _saveData() async {
    if (_prefs == null) return;
    final key = _keyController.text.trim();
    final value = _valueController.text.trim();
    if (key.isEmpty || value.isEmpty) {
      setState(() => _statusMessage = '键和值不能为空');
      return;
    }
    await _prefs!.setString(key, value);
    _keyController.clear();
    _valueController.clear();
    setState(() => _statusMessage = '保存成功!');
    _loadAllData();
  }

  Future<void> _removeData(String key) async {
    if (_prefs == null) return;
    await _prefs!.remove(key);
    setState(() => _statusMessage = '已删除: $key');
    _loadAllData();
  }

  Future<void> _clearAll() async {
    if (_prefs == null) return;
    await _prefs!.clear();
    setState(() => _statusMessage = '已清空所有数据');
    _loadAllData();
  }

  @override
  void dispose() {
    _keyController.dispose();
    _valueController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('本地存储')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'SharedPreferences',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                  ),
                  const SizedBox(height: 4),
                  const Text('轻量级键值对存储，适合简单配置和偏好设置'),
                  const Divider(height: 24),
                  TextField(
                    controller: _keyController,
                    decoration: const InputDecoration(
                      labelText: 'Key',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                  const SizedBox(height: 12),
                  TextField(
                    controller: _valueController,
                    decoration: const InputDecoration(
                      labelText: 'Value',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: FilledButton(
                          onPressed: _saveData,
                          child: const Text('保存'),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: OutlinedButton(
                          onPressed: _clearAll,
                          child: const Text('清空全部'),
                        ),
                      ),
                    ],
                  ),
                  if (_statusMessage.isNotEmpty) ...[
                    const SizedBox(height: 12),
                    Container(
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        color: Theme.of(context)
                            .colorScheme
                            .primaryContainer
                            .withValues(alpha: 0.3),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(_statusMessage),
                    ),
                  ],
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          Text(
            '已存储的数据 (${_allData.length})',
            style: Theme.of(context).textTheme.titleSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
          ),
          const SizedBox(height: 8),
          if (_allData.isEmpty)
            const Card(
              child: Padding(
                padding: EdgeInsets.all(24),
                child: Center(
                  child: Column(
                    children: [
                      Icon(Icons.storage, size: 48, color: Colors.grey),
                      SizedBox(height: 8),
                      Text('暂无数据', style: TextStyle(color: Colors.grey)),
                    ],
                  ),
                ),
              ),
            )
          else
            ..._allData.entries.map((entry) => Card(
                  child: ListTile(
                    title: Text(entry.key,
                        style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text(entry.value),
                    trailing: IconButton(
                      icon: const Icon(Icons.delete_outline, color: Colors.red),
                      onPressed: () => _removeData(entry.key),
                    ),
                  ),
                )),
          const SizedBox(height: 16),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '存储方案对比',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                  ),
                  const Divider(height: 24),
                  _StorageComparisonRow(
                    name: 'SharedPreferences',
                    type: '键值对',
                    scene: '配置/偏好/Token',
                    icon: Icons.key,
                  ),
                  _StorageComparisonRow(
                    name: 'SQLite (sqflite)',
                    type: '关系型数据库',
                    scene: '结构化数据/查询',
                    icon: Icons.table_chart,
                  ),
                  _StorageComparisonRow(
                    name: 'Hive',
                    type: 'NoSQL 数据库',
                    scene: '高性能本地存储',
                    icon: Icons.bolt,
                  ),
                  _StorageComparisonRow(
                    name: '文件存储',
                    type: '文件读写',
                    scene: '图片/文档/缓存',
                    icon: Icons.insert_drive_file,
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _StorageComparisonRow extends StatelessWidget {
  final String name;
  final String type;
  final String scene;
  final IconData icon;

  const _StorageComparisonRow({
    required this.name,
    required this.type,
    required this.scene,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        children: [
          Icon(icon, size: 20, color: Theme.of(context).colorScheme.primary),
          const SizedBox(width: 12),
          Expanded(
            flex: 2,
            child: Text(name, style: const TextStyle(fontWeight: FontWeight.w600)),
          ),
          Expanded(
            flex: 1,
            child: Text(type, style: TextStyle(color: Theme.of(context).disabledColor)),
          ),
          Expanded(
            flex: 1,
            child: Text(scene, style: TextStyle(color: Theme.of(context).disabledColor)),
          ),
        ],
      ),
    );
  }
}
