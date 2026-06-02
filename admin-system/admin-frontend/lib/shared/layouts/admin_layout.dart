import 'package:flutter/material.dart';

class AdminLayout extends StatelessWidget {
  final Widget child;

  const AdminLayout({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          const _Sidebar(),
          Expanded(
            child: Column(
              children: [
                const _TopBar(),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: child,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _Sidebar extends StatefulWidget {
  const _Sidebar();

  @override
  State<_Sidebar> createState() => _SidebarState();
}

class _SidebarState extends State<_Sidebar> {
  bool _collapsed = false;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final width = _collapsed ? 64.0 : 240.0;

    return AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      width: width,
      decoration: BoxDecoration(
        color: theme.colorScheme.surface,
        border: Border(
          right: BorderSide(color: theme.colorScheme.outlineVariant),
        ),
      ),
      child: Column(
        children: [
          Container(
            height: 64,
            alignment: Alignment.center,
            child: _collapsed
                ? Icon(Icons.dashboard, color: theme.colorScheme.primary)
                : Text(
                    'Admin System',
                    style: theme.textTheme.titleMedium?.copyWith(
                      color: theme.colorScheme.primary,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
          ),
          const Divider(height: 1),
          Expanded(
            child: ListView(
              padding: EdgeInsets.zero,
              children: _buildMenuItems(context),
            ),
          ),
          IconButton(
            icon: Icon(_collapsed ? Icons.chevron_right : Icons.chevron_left),
            onPressed: () => setState(() => _collapsed = !_collapsed),
          ),
        ],
      ),
    );
  }

  List<Widget> _buildMenuItems(BuildContext context) {
    final items = [
      _MenuItem(Icons.dashboard_outlined, '首页', '/dashboard'),
      _MenuItem(Icons.people_outlined, '用户管理', '/system/user'),
      _MenuItem(Icons.admin_panel_settings_outlined, '角色管理', '/system/role'),
      _MenuItem(Icons.menu_outlined, '菜单管理', '/system/menu'),
      _MenuItem(Icons.account_tree_outlined, '部门管理', '/system/dept'),
      _MenuItem(Icons.badge_outlined, '岗位管理', '/system/post'),
      _MenuItem(Icons.book_outlined, '字典管理', '/system/dict'),
      _MenuItem(Icons.settings_outlined, '参数设置', '/system/config'),
      _MenuItem(Icons.notifications_outlined, '通知公告', '/system/notice'),
      _MenuItem(Icons.history_outlined, '操作日志', '/monitor/operlog'),
      _MenuItem(Icons.login_outlined, '登录日志', '/monitor/loginlog'),
    ];

    return items.map((item) {
      final currentPath = GoRouterState.of(context).matchedLocation;
      final isActive = currentPath == item.route;

      return ListTile(
        leading: Icon(item.icon, size: 20),
        title: _collapsed ? null : Text(item.label, style: const TextStyle(fontSize: 14)),
        selected: isActive,
        dense: true,
        onTap: () => context.go(item.route),
      );
    }).toList();
  }
}

class _TopBar extends StatelessWidget {
  const _TopBar();

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Container(
      height: 64,
      padding: const EdgeInsets.symmetric(horizontal: 16),
      decoration: BoxDecoration(
        color: theme.colorScheme.surface,
        border: Border(
          bottom: BorderSide(color: theme.colorScheme.outlineVariant),
        ),
      ),
      child: Row(
        children: [
          Expanded(child: Text('首页', style: theme.textTheme.titleMedium)),
          IconButton(
            icon: const Icon(Icons.notifications_outlined),
            onPressed: () {},
          ),
          PopupMenuButton(
            offset: const Offset(0, 40),
            child: const CircleAvatar(child: Icon(Icons.person)),
            itemBuilder: (context) => [
              const PopupMenuItem(value: 'profile', child: Text('个人中心')),
              const PopupMenuItem(value: 'logout', child: Text('退出登录')),
            ],
            onSelected: (value) {
              if (value == 'logout') {
                context.go('/login');
              }
            },
          ),
        ],
      ),
    );
  }
}

class _MenuItem {
  final IconData icon;
  final String label;
  final String route;

  const _MenuItem(this.icon, this.label, this.route);
}
