import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/constants/api_constants.dart';

final menuListProvider = FutureProvider<List<dynamic>>((ref) async {
  final dio = ref.watch(dioProvider);
  final response = await dio.get(ApiConstants.menuList);
  return response.data['data'] ?? [];
});

class MenuListPage extends ConsumerWidget {
  const MenuListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncData = ref.watch(menuListProvider);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Text('菜单管理', style: Theme.of(context).textTheme.headlineSmall),
            const Spacer(),
            FilledButton.icon(onPressed: () {}, icon: const Icon(Icons.add), label: const Text('新增')),
          ],
        ),
        const SizedBox(height: 16),
        Expanded(
          child: asyncData.when(
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (e, _) => Center(child: Text('加载失败: $e')),
            data: (list) {
              return _MenuTree(menus: list.cast<Map<String, dynamic>>());
            },
          ),
        ),
      ],
    );
  }
}

class _MenuTree extends StatelessWidget {
  final List<Map<String, dynamic>> menus;
  const _MenuTree({required this.menus});

  @override
  Widget build(BuildContext context) {
    final roots = menus.where((m) => m['parentId'] == 0).toList();
    return ListView.builder(
      itemCount: roots.length,
      itemBuilder: (context, index) => _MenuTile(menu: roots[index], allMenus: menus),
    );
  }
}

class _MenuTile extends StatefulWidget {
  final Map<String, dynamic> menu;
  final List<Map<String, dynamic>> allMenus;
  const _MenuTile({required this.menu, required this.allMenus});

  @override
  State<_MenuTile> createState() => _MenuTileState();
}

class _MenuTileState extends State<_MenuTile> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    final children = widget.allMenus.where((m) => m['parentId'] == widget.menu['id']).toList();
    final hasChildren = children.isNotEmpty;

    return Column(
      children: [
        ListTile(
          leading: Icon(_getIcon(widget.menu['icon'])),
          title: Text(widget.menu['menuName'] ?? ''),
          subtitle: Text('${widget.menu['path'] ?? ''} | ${widget.menu['perms'] ?? ''}'),
          trailing: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextButton(onPressed: () {}, child: const Text('编辑')),
              TextButton(onPressed: () {}, child: const Text('新增')),
              TextButton(onPressed: () {}, child: const Text('删除')),
              if (hasChildren)
                IconButton(
                  icon: Icon(_expanded ? Icons.expand_less : Icons.expand_more),
                  onPressed: () => setState(() => _expanded = !_expanded),
                ),
            ],
          ),
        ),
        if (_expanded && hasChildren)
          Padding(
            padding: const EdgeInsets.only(left: 24),
            child: Column(
              children: children.map((c) => _MenuTile(menu: c, allMenus: widget.allMenus)).toList(),
            ),
          ),
      ],
    );
  }

  IconData _getIcon(String? icon) {
    if (icon == null || icon.isEmpty) return Icons.folder_outlined;
    return Icons.folder_outlined;
  }
}
