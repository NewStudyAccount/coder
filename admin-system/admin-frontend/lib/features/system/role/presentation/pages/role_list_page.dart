import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/constants/api_constants.dart';
import '../../../../shared/widgets/admin_data_table.dart';

final roleListProvider = FutureProvider.family<Map<String, dynamic>, int>((ref, page) async {
  final dio = ref.watch(dioProvider);
  final response = await dio.get(ApiConstants.roleList, queryParameters: {
    'pageNum': page,
    'pageSize': 10,
  });
  return response.data['data'];
});

class RoleListPage extends ConsumerWidget {
  const RoleListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncData = ref.watch(roleListProvider(1));

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
            Text('角色管理', style: Theme.of(context).textTheme.headlineSmall),
            const SizedBox(height: 16),
            Expanded(
              child: AdminDataTable<Map<String, dynamic>>(
                data: list.cast<Map<String, dynamic>>(),
                columns: [
                  AdminDataColumn(label: '角色名称', builder: (r) => Text(r['roleName'] ?? '')),
                  AdminDataColumn(label: '角色标识', builder: (r) => Text(r['roleKey'] ?? '')),
                  AdminDataColumn(label: '排序', builder: (r) => Text(r['roleSort']?.toString() ?? '')),
                  AdminDataColumn(label: '状态', builder: (r) => _StatusTag(status: r['status'])),
                  AdminDataColumn(label: '创建时间', builder: (r) => Text(r['createTime'] ?? '')),
                  AdminDataColumn(
                    label: '操作',
                    builder: (r) => Row(
                      children: [
                        TextButton(onPressed: () {}, child: const Text('编辑')),
                        TextButton(onPressed: () {}, child: const Text('删除')),
                        TextButton(onPressed: () {}, child: const Text('分配菜单')),
                      ],
                    ),
                  ),
                ],
                totalPages: totalPages,
                currentPage: 1,
                total: total,
                onPageChanged: (page) {
                  ref.invalidate(roleListProvider(page));
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
