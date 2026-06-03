import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:admin/shared/widgets/admin_data_table.dart';
import 'package:admin/shared/widgets/permission_widget.dart';

class JobLogListPage extends ConsumerStatefulWidget {
  const JobLogListPage({super.key});

  @override
  ConsumerState<JobLogListPage> createState() => _JobLogListPageState();
}

class _JobLogListPageState extends ConsumerState<JobLogListPage> {
  List<Map<String, dynamic>> _logs = [];
  bool _isLoading = true;
  int _currentPage = 1;
  int _total = 0;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    await Future.delayed(const Duration(milliseconds: 500));
    setState(() {
      _logs = [
        {'id': 1, 'jobName': '系统清理任务', 'jobGroup': 'DEFAULT', 'invokeTarget': 'cleanTask.execute()', 'jobMessage': '执行成功', 'status': 1, 'startTime': '2024-01-15 02:00:00', 'endTime': '2024-01-15 02:00:05'},
        {'id': 2, 'jobName': '数据备份任务', 'jobGroup': 'DEFAULT', 'invokeTarget': 'backupTask.execute()', 'jobMessage': '执行成功', 'status': 1, 'startTime': '2024-01-15 03:00:00', 'endTime': '2024-01-15 03:00:30'},
        {'id': 3, 'jobName': '系统清理任务', 'jobGroup': 'DEFAULT', 'invokeTarget': 'cleanTask.execute()', 'jobMessage': '执行失败: 连接超时', 'status': 0, 'startTime': '2024-01-13 02:00:00', 'endTime': '2024-01-13 02:00:02'},
      ];
      _total = 3;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    final columns = [
      AdminDataColumn<Map<String, dynamic>>(label: '日志ID', getValue: (r) => r['id'].toString()),
      AdminDataColumn<Map<String, dynamic>>(label: '任务名称', getValue: (r) => r['jobName']),
      AdminDataColumn<Map<String, dynamic>>(label: '任务组名', getValue: (r) => r['jobGroup']),
      AdminDataColumn<Map<String, dynamic>>(label: '调用目标', getValue: (r) => r['invokeTarget']),
      AdminDataColumn<Map<String, dynamic>>(label: '日志信息', getValue: (r) => r['jobMessage']),
      AdminDataColumn<Map<String, dynamic>>(
        label: '状态',
        getValue: (r) => r['status'] == 1 ? '成功' : '失败',
        cellBuilder: (context, record) => Chip(
          label: Text(record['status'] == 1 ? '成功' : '失败'),
          backgroundColor: record['status'] == 1 ? Colors.green.shade100 : Colors.red.shade100,
          labelStyle: TextStyle(color: record['status'] == 1 ? Colors.green.shade800 : Colors.red.shade800),
        ),
      ),
      AdminDataColumn<Map<String, dynamic>>(label: '开始时间', getValue: (r) => r['startTime']),
      AdminDataColumn<Map<String, dynamic>>(label: '结束时间', getValue: (r) => r['endTime']),
    ];

    return Scaffold(
      body: AdminDataTable<Map<String, dynamic>>(
        data: _logs,
        columns: columns,
        totalPages: (_total / 10).ceil(),
        currentPage: _currentPage,
        total: _total,
        onPageChanged: (page) => setState(() => _currentPage = page),
        onRefresh: _loadData,
        isLoading: _isLoading,
        toolbarActions: [
          PermissionButton(
            permission: 'devtools:job:remove',
            label: '清空日志',
            isDestructive: true,
            onPressed: () {
              setState(() => _logs.clear());
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('日志已清空')),
              );
            },
          ),
        ],
      ),
    );
  }
}
