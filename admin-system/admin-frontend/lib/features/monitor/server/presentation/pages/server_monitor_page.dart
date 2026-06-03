import 'package:flutter/material.dart';

class ServerMonitorPage extends StatefulWidget {
  const ServerMonitorPage({super.key});

  @override
  State<ServerMonitorPage> createState() => _ServerMonitorPageState();
}

class _ServerMonitorPageState extends State<ServerMonitorPage> {
  Map<String, dynamic> _serverInfo = {};
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    await Future.delayed(const Duration(milliseconds: 500));
    setState(() {
      _serverInfo = {
        'osName': 'Windows 11',
        'osArch': 'amd64',
        'osVersion': '10.0',
        'processors': 16,
        'totalMemory': 4294967296,
        'usedMemory': 2147483648,
        'maxMemory': 4294967296,
        'freeMemory': 2147483648,
        'memoryUsage': '50.0%',
        'jvm': {
          'name': 'OpenJDK 64-Bit Server VM',
          'version': '17.0.8',
          'usedHeap': 1073741824,
          'maxHeap': 2147483648,
          'heapUsage': '50.0%',
        },
        'disks': [
          {'path': 'C:\\', 'total': 536870912000, 'used': 268435456000, 'free': 268435456000, 'usage': '50.0%'},
          {'path': 'D:\\', 'total': 1073741824000, 'used': 536870912000, 'free': 536870912000, 'usage': '50.0%'},
        ],
      };
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: _loadData,
        child: ListView(
          padding: const EdgeInsets.all(24),
          children: [
            _buildSection('操作系统信息', [
              _buildInfoRow('操作系统', _serverInfo['osName']),
              _buildInfoRow('系统架构', _serverInfo['osArch']),
              _buildInfoRow('系统版本', _serverInfo['osVersion']),
              _buildInfoRow('处理器数', '${_serverInfo['processors']} 核'),
            ]),
            const SizedBox(height: 24),
            _buildSection('JVM 信息', [
              _buildInfoRow('JVM 名称', _serverInfo['jvm']?['name']),
              _buildInfoRow('JVM 版本', _serverInfo['jvm']?['version']),
              _buildInfoRow('堆内存使用', _serverInfo['jvm']?['heapUsage']),
              _buildProgressRow('堆内存', _serverInfo['jvm']?['usedHeap'] ?? 0, _serverInfo['jvm']?['maxHeap'] ?? 1),
            ]),
            const SizedBox(height: 24),
            _buildSection('内存信息', [
              _buildInfoRow('内存使用率', _serverInfo['memoryUsage']),
              _buildProgressRow('内存', _serverInfo['usedMemory'] ?? 0, _serverInfo['maxMemory'] ?? 1),
              _buildInfoRow('总内存', _formatBytes(_serverInfo['totalMemory'])),
              _buildInfoRow('已用内存', _formatBytes(_serverInfo['usedMemory'])),
              _buildInfoRow('空闲内存', _formatBytes(_serverInfo['freeMemory'])),
              _buildInfoRow('最大可用', _formatBytes(_serverInfo['maxMemory'])),
            ]),
            const SizedBox(height: 24),
            _buildSection('磁盘信息', [
              ...(_serverInfo['disks'] as List).map((disk) => Column(
                    children: [
                      _buildInfoRow('盘符路径', disk['path']),
                      _buildInfoRow('使用率', disk['usage']),
                      _buildProgressRow('磁盘', disk['used'], disk['total']),
                      _buildInfoRow('总大小', _formatBytes(disk['total'])),
                      _buildInfoRow('已用', _formatBytes(disk['used'])),
                      _buildInfoRow('可用', _formatBytes(disk['free'])),
                      const Divider(),
                    ],
                  )),
            ]),
          ],
        ),
      ),
    );
  }

  Widget _buildSection(String title, List<Widget> children) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: Theme.of(context).textTheme.titleMedium),
            const Divider(),
            ...children,
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, dynamic value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          SizedBox(width: 120, child: Text(label, style: Theme.of(context).textTheme.bodySmall)),
          Expanded(child: Text(value?.toString() ?? '-', style: Theme.of(context).textTheme.bodyMedium)),
        ],
      ),
    );
  }

  Widget _buildProgressRow(String label, num used, num total) {
    final ratio = total > 0 ? used / total : 0.0;
    final color = ratio > 0.9
        ? Colors.red
        : ratio > 0.7
            ? Colors.orange
            : Colors.green;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          SizedBox(width: 120, child: Text(label, style: Theme.of(context).textTheme.bodySmall)),
          Expanded(
            child: LinearProgressIndicator(
              value: ratio.toDouble(),
              backgroundColor: Colors.grey.shade200,
              color: color,
            ),
          ),
          const SizedBox(width: 8),
          Text('${(ratio * 100).toStringAsFixed(1)}%', style: Theme.of(context).textTheme.bodySmall),
        ],
      ),
    );
  }

  String _formatBytes(num bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1048576) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    if (bytes < 1073741824) return '${(bytes / 1048576).toStringAsFixed(1)} MB';
    return '${(bytes / 1073741824).toStringAsFixed(1)} GB';
  }
}
