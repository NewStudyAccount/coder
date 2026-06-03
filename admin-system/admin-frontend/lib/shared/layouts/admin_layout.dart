import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class AdminLayout extends StatelessWidget {
  final Widget child;

  const AdminLayout({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.sizeOf(context).width;
    final isMobile = screenWidth < 768;

    return Scaffold(
      body: Row(
        children: [
          _Sidebar(isMobile: isMobile),
          Expanded(
            child: Column(
              children: [
                const _TopBar(),
                const _TabBar(),
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
  final bool isMobile;

  const _Sidebar({required this.isMobile});

  @override
  State<_Sidebar> createState() => _SidebarState();
}

class _SidebarState extends State<_Sidebar> {
  bool _collapsed = false;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final collapsed = widget.isMobile || _collapsed;
    final width = collapsed ? 64.0 : 240.0;

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
            child: collapsed
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
          if (!widget.isMobile)
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
      _MenuGroup('系统管理', Icons.settings_outlined, [
        _MenuItem(Icons.people_outlined, '用户管理', '/system/user'),
        _MenuItem(Icons.admin_panel_settings_outlined, '角色管理', '/system/role'),
        _MenuItem(Icons.menu_outlined, '菜单管理', '/system/menu'),
        _MenuItem(Icons.account_tree_outlined, '部门管理', '/system/dept'),
        _MenuItem(Icons.badge_outlined, '岗位管理', '/system/post'),
        _MenuItem(Icons.book_outlined, '字典管理', '/system/dict'),
        _MenuItem(Icons.tune_outlined, '参数设置', '/system/config'),
        _MenuItem(Icons.notifications_outlined, '通知公告', '/system/notice'),
      ]),
      _MenuGroup('系统监控', Icons.monitor_outlined, [
        _MenuItem(Icons.history_outlined, '操作日志', '/monitor/operlog'),
        _MenuItem(Icons.login_outlined, '登录日志', '/monitor/loginlog'),
        _MenuItem(Icons.people_alt_outlined, '在线用户', '/monitor/online'),
        _MenuItem(Icons.dns_outlined, '服务监控', '/monitor/server'),
      ]),
      _MenuGroup('开发工具', Icons.build_outlined, [
        _MenuItem(Icons.code_outlined, '代码生成', '/devtools/gen'),
        _MenuItem(Icons.schedule_outlined, '定时任务', '/devtools/job'),
        _MenuItem(Icons.list_alt_outlined, '任务日志', '/devtools/job-log'),
      ]),
    ];

    return items.map((item) {
      if (item is _MenuGroup) {
        return _buildMenuGroup(context, item);
      }
      return _buildMenuItem(context, item as _MenuItem);
    }).toList();
  }

  Widget _buildMenuGroup(BuildContext context, _MenuGroup group) {
    final currentPath = GoRouterState.of(context).matchedLocation;
    final isGroupActive = group.items.any((item) => currentPath == item.route);

    return ExpansionTile(
      initiallyExpanded: isGroupActive,
      leading: Icon(group.icon, size: 20),
      title: _collapsed ? null : Text(group.label, style: const TextStyle(fontSize: 14)),
      dense: true,
      children: group.items.map((item) {
        return _buildMenuItem(context, item, indent: true);
      }).toList(),
    );
  }

  Widget _buildMenuItem(BuildContext context, _MenuItem item, {bool indent = false}) {
    final currentPath = GoRouterState.of(context).matchedLocation;
    final isActive = currentPath == item.route;

    return ListTile(
      leading: indent ? const SizedBox(width: 20) : Icon(item.icon, size: 20),
      title: _collapsed ? null : Text(item.label, style: const TextStyle(fontSize: 14)),
      selected: isActive,
      dense: true,
      contentPadding: EdgeInsets.only(left: indent ? 24 : 16, right: 16),
      onTap: () => context.go(item.route),
    );
  }
}

class _TopBar extends StatelessWidget {
  const _TopBar();

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final currentPath = GoRouterState.of(context).matchedLocation;

    return Container(
      height: 48,
      padding: const EdgeInsets.symmetric(horizontal: 16),
      decoration: BoxDecoration(
        color: theme.colorScheme.surface,
        border: Border(
          bottom: BorderSide(color: theme.colorScheme.outlineVariant),
        ),
      ),
      child: Row(
        children: [
          _buildBreadcrumb(context, currentPath),
          const Spacer(),
          IconButton(
            icon: const Icon(Icons.notifications_outlined, size: 20),
            onPressed: () {},
          ),
          PopupMenuButton(
            offset: const Offset(0, 40),
            child: const CircleAvatar(radius: 14, child: Icon(Icons.person, size: 16)),
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

  Widget _buildBreadcrumb(BuildContext context, String currentPath) {
    final breadcrumbs = _getBreadcrumbs(currentPath);
    return Row(
      children: breadcrumbs.asMap().entries.map((entry) {
        final index = entry.key;
        final label = entry.value;
        return Row(
          children: [
            if (index > 0)
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 4),
                child: Icon(Icons.chevron_right, size: 16, color: Theme.of(context).hintColor),
              ),
            Text(
              label,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: index == breadcrumbs.length - 1
                        ? Theme.of(context).colorScheme.onSurface
                        : Theme.of(context).hintColor,
                  ),
            ),
          ],
        );
      }).toList(),
    );
  }

  List<String> _getBreadcrumbs(String path) {
    final map = {
      '/dashboard': ['首页'],
      '/system/user': ['系统管理', '用户管理'],
      '/system/role': ['系统管理', '角色管理'],
      '/system/menu': ['系统管理', '菜单管理'],
      '/system/dept': ['系统管理', '部门管理'],
      '/system/post': ['系统管理', '岗位管理'],
      '/system/dict': ['系统管理', '字典管理'],
      '/system/config': ['系统管理', '参数设置'],
      '/system/notice': ['系统管理', '通知公告'],
      '/monitor/operlog': ['系统监控', '操作日志'],
      '/monitor/loginlog': ['系统监控', '登录日志'],
      '/monitor/online': ['系统监控', '在线用户'],
      '/monitor/server': ['系统监控', '服务监控'],
      '/devtools/gen': ['开发工具', '代码生成'],
      '/devtools/job': ['开发工具', '定时任务'],
      '/devtools/job-log': ['开发工具', '任务日志'],
    };
    return map[path] ?? [path];
  }
}

class _TabBar extends StatefulWidget {
  const _TabBar();

  @override
  State<_TabBar> createState() => _TabBarState();
}

class _TabBarState extends State<_TabBar> {
  static final List<_TabInfo> _tabs = [];
  static String _activeRoute = '/dashboard';

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final currentPath = GoRouterState.of(context).matchedLocation;
    if (currentPath != _activeRoute) {
      _activeRoute = currentPath;
      final label = _getTabLabel(currentPath);
      final existing = _tabs.where((t) => t.route == currentPath);
      if (existing.isEmpty && currentPath != '/login') {
        _tabs.add(_TabInfo(route: currentPath, label: label));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_tabs.isEmpty) return const SizedBox.shrink();

    final theme = Theme.of(context);

    return Container(
      height: 36,
      decoration: BoxDecoration(
        color: theme.colorScheme.surfaceContainerLow,
        border: Border(
          bottom: BorderSide(color: theme.colorScheme.outlineVariant),
        ),
      ),
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 8),
        itemCount: _tabs.length,
        separatorBuilder: (_, __) => const SizedBox(width: 2),
        itemBuilder: (context, index) {
          final tab = _tabs[index];
          final isActive = tab.route == _activeRoute;

          return InkWell(
            onTap: () => context.go(tab.route),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12),
              decoration: isActive
                  ? BoxDecoration(
                      color: theme.colorScheme.surface,
                      border: Border(
                        bottom: BorderSide(color: theme.colorScheme.primary, width: 2),
                      ),
                    )
                  : null,
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    tab.label,
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: isActive ? theme.colorScheme.primary : theme.hintColor,
                      fontWeight: isActive ? FontWeight.w600 : FontWeight.normal,
                    ),
                  ),
                  if (_tabs.length > 1) ...[
                    const SizedBox(width: 4),
                    GestureDetector(
                      onTap: () => _closeTab(index),
                      child: Icon(Icons.close, size: 14, color: theme.hintColor),
                    ),
                  ],
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  void _closeTab(int index) {
    setState(() {
      final tab = _tabs[index];
      _tabs.removeAt(index);
      if (tab.route == _activeRoute) {
        final newTab = _tabs.isNotEmpty ? _tabs.last : _TabInfo(route: '/dashboard', label: '首页');
        _activeRoute = newTab.route;
        context.go(newTab.route);
      }
    });
  }

  String _getTabLabel(String path) {
    final map = {
      '/dashboard': '首页',
      '/system/user': '用户管理',
      '/system/role': '角色管理',
      '/system/menu': '菜单管理',
      '/system/dept': '部门管理',
      '/system/post': '岗位管理',
      '/system/dict': '字典管理',
      '/system/config': '参数设置',
      '/system/notice': '通知公告',
      '/monitor/operlog': '操作日志',
      '/monitor/loginlog': '登录日志',
      '/monitor/online': '在线用户',
      '/monitor/server': '服务监控',
      '/devtools/gen': '代码生成',
      '/devtools/job': '定时任务',
      '/devtools/job-log': '任务日志',
    };
    return map[path] ?? path;
  }
}

class _TabInfo {
  final String route;
  final String label;

  _TabInfo({required this.route, required this.label});
}

class _MenuItem {
  final IconData icon;
  final String label;
  final String route;

  const _MenuItem(this.icon, this.label, this.route);
}

class _MenuGroup {
  final String label;
  final IconData icon;
  final List<_MenuItem> items;

  const _MenuGroup(this.label, this.icon, this.items);
}
