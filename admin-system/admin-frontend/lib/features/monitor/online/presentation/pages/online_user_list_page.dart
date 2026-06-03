import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:admin/shared/widgets/admin_data_table.dart';
import 'package:admin/shared/widgets/permission_widget.dart';

class OnlineUserListPage extends ConsumerStatefulWidget {
  const OnlineUserListPage({super.key});

  @override
  ConsumerState<OnlineUserListPage> createState() => _OnlineUserListPageState();
}

class _OnlineUserListPageState extends ConsumerState<OnlineUserListPage> {
  List<Map<String, dynamic>> _onlineUsers = [];
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
      _onlineUsers = [
        {'userId': 1, 'username': 'admin', 'deptName': '研发部门', 'ipaddr': '127.0.0.1', 'loginLocation': '内网IP', 'browser': 'Chrome', 'os': 'Windows 10', 'loginTime': '2024-01-01 10:00:00'},
        {'userId': 2, 'username': 'ry', 'deptName': '测试部门', 'ipaddr': '192.168.1.100', 'loginLocation': '内网IP', 'browser': 'Firefox', 'os': 'Windows 11', 'loginTime': '2024-01-01 11:00:00'},
      ];
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    final columns = [
      AdminDataColumn<Map<String, dynamic>>(label: '用户ID', getValue: (r) => r['userId'].toString()),
      AdminDataColumn<Map<String, dynamic>>(label: '用户名', getValue: (r) => r['username']),
      AdminDataColumn<Map<String, dynamic>>(label: '部门', getValue: (r) => r['deptName']),
      AdminDataColumn<Map<String, dynamic>>(label: '登录IP', getValue: (r) => r['ipaddr']),
      AdminDataColumn<Map<String, dynamic>>(label: '登录地点', getValue: (r) => r['loginLocation']),
      AdminDataColumn<Map<String, dynamic>>(label: '浏览器', getValue: (r) => r['browser']),
      AdminDataColumn<Map<String, dynamic>>(label: '操作系统', getValue: (r) => r['os']),
      AdminDataColumn<Map<String, dynamic>>(label: '登录时间', getValue: (r) => r['loginTime']),
      AdminDataColumn<Map<String, dynamic>>(
        label: '操作',
        getValue: (r) => '',
        cellBuilder: (context, record) => PermissionButton(
          permission: 'monitor:online:forceLogout',
          label: '强退',
          isDestructive: true,
          onPressed: () => _forceLogout(record),
        ),
      ),
    ];

    return Scaffold(
      body: AdminDataTable<Map<String, dynamic>>(
        data: _onlineUsers,
        columns: columns,
        totalPages: 1,
        currentPage: 1,
        total: _onlineUsers.length,
        onPageChanged: (_) {},
        onRefresh: _loadData,
        isLoading: _isLoading,
      ),
    );
  }

  void _forceLogout(Map<String, dynamic> user) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('确认强退'),
        content: Text('确定要强制用户 ${user['username']} 下线吗？'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('取消')),
          FilledButton(
            onPressed: () {
              Navigator.pop(ctx);
              setState(() => _onlineUsers.removeWhere((u) => u['userId'] == user['userId']));
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('已强制用户 ${user['username']} 下线')),
              );
            },
            child: const Text('确认'),
          ),
        ],
      ),
    );
  }
}
