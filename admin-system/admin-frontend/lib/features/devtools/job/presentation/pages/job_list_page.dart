import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:admin/shared/widgets/admin_data_table.dart';
import 'package:admin/shared/widgets/admin_dialog.dart';
import 'package:admin/shared/widgets/permission_widget.dart';

class JobListPage extends ConsumerStatefulWidget {
  const JobListPage({super.key});

  @override
  ConsumerState<JobListPage> createState() => _JobListPageState();
}

class _JobListPageState extends ConsumerState<JobListPage> {
  List<Map<String, dynamic>> _jobs = [];
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
      _jobs = [
        {'id': 1, 'jobName': '系统清理任务', 'jobGroup': 'DEFAULT', 'invokeTarget': 'cleanTask.execute()', 'cronExpression': '0 0 2 * * ?', 'status': 1, 'createTime': '2024-01-01'},
        {'id': 2, 'jobName': '数据备份任务', 'jobGroup': 'DEFAULT', 'invokeTarget': 'backupTask.execute()', 'cronExpression': '0 0 3 * * ?', 'status': 1, 'createTime': '2024-01-01'},
        {'id': 3, 'jobName': '日志清理任务', 'jobGroup': 'SYSTEM', 'invokeTarget': 'logCleanTask.execute()', 'cronExpression': '0 0 4 * * ?', 'status': 0, 'createTime': '2024-01-01'},
      ];
      _total = 3;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    final columns = [
      AdminDataColumn<Map<String, dynamic>>(label: '任务ID', getValue: (r) => r['id'].toString()),
      AdminDataColumn<Map<String, dynamic>>(label: '任务名称', getValue: (r) => r['jobName']),
      AdminDataColumn<Map<String, dynamic>>(label: '任务组名', getValue: (r) => r['jobGroup']),
      AdminDataColumn<Map<String, dynamic>>(label: '调用目标', getValue: (r) => r['invokeTarget']),
      AdminDataColumn<Map<String, dynamic>>(label: 'Cron表达式', getValue: (r) => r['cronExpression']),
      AdminDataColumn<Map<String, dynamic>>(
        label: '状态',
        getValue: (r) => r['status'] == 1 ? '启用' : '停用',
        cellBuilder: (context, record) => Switch(
          value: record['status'] == 1,
          onChanged: (val) => _changeStatus(record, val ? 1 : 0),
        ),
      ),
      AdminDataColumn<Map<String, dynamic>>(
        label: '操作',
        getValue: (r) => '',
        cellBuilder: (context, record) => Row(
          children: [
            PermissionButton(
              permission: 'devtools:job:edit',
              label: '编辑',
              onPressed: () => _editJob(record),
            ),
            const SizedBox(width: 8),
            PermissionButton(
              permission: 'devtools:job:edit',
              label: '执行一次',
              onPressed: () => _runOnce(record),
            ),
            const SizedBox(width: 8),
            PermissionButton(
              permission: 'devtools:job:list',
              label: '日志',
              onPressed: () => _viewLog(record),
            ),
            const SizedBox(width: 8),
            PermissionButton(
              permission: 'devtools:job:remove',
              label: '删除',
              isDestructive: true,
              onPressed: () => _deleteJob(record),
            ),
          ],
        ),
      ),
    ];

    return Scaffold(
      body: AdminDataTable<Map<String, dynamic>>(
        data: _jobs,
        columns: columns,
        totalPages: (_total / 10).ceil(),
        currentPage: _currentPage,
        total: _total,
        onPageChanged: (page) => setState(() => _currentPage = page),
        onRefresh: _loadData,
        onAdd: _addJob,
        isLoading: _isLoading,
      ),
    );
  }

  void _addJob() {
    AdminDialog.showForm(
      context,
      title: '新增任务',
      fields: [
        {'name': 'jobName', 'label': '任务名称', 'type': 'text', 'required': true},
        {'name': 'jobGroup', 'label': '任务组名', 'type': 'text', 'defaultValue': 'DEFAULT'},
        {'name': 'invokeTarget', 'label': '调用目标', 'type': 'text', 'required': true},
        {'name': 'cronExpression', 'label': 'Cron表达式', 'type': 'text', 'required': true},
      ],
      onConfirm: (values) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('已创建任务: ${values['jobName']}')),
        );
      },
    );
  }

  void _editJob(Map<String, dynamic> job) {
    AdminDialog.showForm(
      context,
      title: '编辑任务',
      initialValues: job,
      fields: [
        {'name': 'jobName', 'label': '任务名称', 'type': 'text', 'required': true},
        {'name': 'jobGroup', 'label': '任务组名', 'type': 'text'},
        {'name': 'invokeTarget', 'label': '调用目标', 'type': 'text', 'required': true},
        {'name': 'cronExpression', 'label': 'Cron表达式', 'type': 'text', 'required': true},
      ],
      onConfirm: (values) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('任务已更新')),
        );
      },
    );
  }

  void _changeStatus(Map<String, dynamic> job, int status) {
    setState(() => job['status'] = status);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('任务已${status == 1 ? '启用' : '停用'}')),
    );
  }

  void _runOnce(Map<String, dynamic> job) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('正在执行任务: ${job['jobName']}')),
    );
  }

  void _viewLog(Map<String, dynamic> job) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('执行日志 - ${job['jobName']}'),
        content: SizedBox(
          width: 600,
          height: 400,
          child: ListView(
            children: [
              _buildLogItem('2024-01-15 02:00:00', '执行成功', 1),
              _buildLogItem('2024-01-14 02:00:00', '执行成功', 1),
              _buildLogItem('2024-01-13 02:00:00', '执行失败: 连接超时', 0),
              _buildLogItem('2024-01-12 02:00:00', '执行成功', 1),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('关闭')),
        ],
      ),
    );
  }

  Widget _buildLogItem(String time, String message, int status) {
    return ListTile(
      leading: Icon(
        status == 1 ? Icons.check_circle : Icons.error,
        color: status == 1 ? Colors.green : Colors.red,
      ),
      title: Text(message),
      subtitle: Text(time, style: const TextStyle(fontSize: 12)),
    );
  }

  void _deleteJob(Map<String, dynamic> job) {
    AdminDialog.showConfirm(
      context,
      title: '确认删除',
      content: '确定要删除任务 ${job['jobName']} 吗？',
      onConfirm: () {
        setState(() => _jobs.removeWhere((j) => j['id'] == job['id']));
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('已删除')),
        );
      },
    );
  }
}
