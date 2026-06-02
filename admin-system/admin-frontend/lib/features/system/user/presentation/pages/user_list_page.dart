import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/constants/api_constants.dart';
import '../../../../shared/widgets/admin_data_table.dart';

final userListProvider = FutureProvider.family<Map<String, dynamic>, int>((ref, page) async {
  final dio = ref.watch(dioProvider);
  final response = await dio.get(ApiConstants.userList, queryParameters: {
    'pageNum': page,
    'pageSize': 10,
  });
  return response.data['data'];
});

class UserListPage extends ConsumerWidget {
  const UserListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncData = ref.watch(userListProvider(1));

    return asyncData.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('加载失败: $e')),
      data: (data) {
        final list = (data['list'] as List?) ?? [];
        final total = data['total'] as int? ?? 0;
        final totalPages = (total / 10).ceil();

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('用户管理', style: Theme.of(context).textTheme.headlineSmall),
            const SizedBox(height: 16),
            Expanded(
              child: AdminDataTable<Map<String, dynamic>>(
                data: list.cast<Map<String, dynamic>>(),
                columns: [
                  AdminDataColumn(label: '用户名', builder: (u) => Text(u['username'] ?? '')),
                  AdminDataColumn(label: '昵称', builder: (u) => Text(u['nickname'] ?? '')),
                  AdminDataColumn(label: '部门', builder: (u) => Text(u['deptId']?.toString() ?? '')),
                  AdminDataColumn(label: '手机号', builder: (u) => Text(u['phone'] ?? '')),
                  AdminDataColumn(label: '状态', builder: (u) => _StatusTag(status: u['status'])),
                  AdminDataColumn(label: '创建时间', builder: (u) => Text(u['createTime'] ?? '')),
                  AdminDataColumn(
                    label: '操作',
                    builder: (u) => Row(
                      children: [
                        TextButton(onPressed: () {}, child: const Text('编辑')),
                        TextButton(onPressed: () {}, child: const Text('删除')),
                        TextButton(onPressed: () {}, child: const Text('重置密码')),
                      ],
                    ),
                  ),
                ],
                totalPages: totalPages,
                currentPage: 1,
                total: total,
                onPageChanged: (page) {
                  ref.invalidate(userListProvider(page));
                },
                onAdd: () {},
              ),
            ),
          ],
        );
      },
    );
  }
}

class _StatusTag extends StatelessWidget {
  final int? status;
  const _StatusTag({this.status});

  @override
  Widget build(BuildContext context) {
    final enabled = status == 1;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: enabled ? Colors.green.shade50 : Colors.red.shade50,
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        enabled ? '启用' : '禁用',
        style: TextStyle(color: enabled ? Colors.green : Colors.red, fontSize: 12),
      ),
    );
  }
}
