import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../app/routes/app_routes.dart';
import '../../app/state/theme_provider.dart';

class LearningModule {
  final String title;
  final String subtitle;
  final IconData icon;
  final Color color;
  final String route;

  LearningModule({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.color,
    required this.route,
  });
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final modules = [
      LearningModule(
        title: 'Widget 目录',
        subtitle: '常用 Widget 使用示例',
        icon: Icons.widgets,
        color: Colors.blue,
        route: AppRoutes.widgetCatalog,
      ),
      LearningModule(
        title: '布局系统',
        subtitle: 'Row / Column / Stack / Flex',
        icon: Icons.dashboard,
        color: Colors.teal,
        route: AppRoutes.layoutDemo,
      ),
      LearningModule(
        title: '状态管理',
        subtitle: 'setState / Provider / InheritedWidget',
        icon: Icons.settings_suggest,
        color: Colors.orange,
        route: AppRoutes.providerDemo,
      ),
      LearningModule(
        title: '计数器',
        subtitle: 'Provider 状态管理实战',
        icon: Icons.exposure_plus_1,
        color: Colors.purple,
        route: AppRoutes.counter,
      ),
      LearningModule(
        title: '待办清单',
        subtitle: '完整 CRUD 实战',
        icon: Icons.checklist,
        color: Colors.green,
        route: AppRoutes.todo,
      ),
      LearningModule(
        title: '网络请求',
        subtitle: 'Dio / HTTP / JSON 解析',
        icon: Icons.cloud,
        color: Colors.indigo,
        route: AppRoutes.networkDemo,
      ),
      LearningModule(
        title: '本地存储',
        subtitle: 'SharedPreferences / SQLite',
        icon: Icons.storage,
        color: Colors.brown,
        route: AppRoutes.storageDemo,
      ),
      LearningModule(
        title: '动画效果',
        subtitle: '隐式动画 / 显式动画 / Hero',
        icon: Icons.animation,
        color: Colors.pink,
        route: AppRoutes.animationDemo,
      ),
      LearningModule(
        title: '路由导航',
        subtitle: '命名路由 / 参数传递 / 返回值',
        icon: Icons.navigation,
        color: Colors.cyan,
        route: AppRoutes.navigationDemo,
      ),
    ];

    return Scaffold(
      appBar: AppBar(
        title: const Text('Flutter 学习'),
        actions: [
          Consumer<ThemeProvider>(
            builder: (_, themeProvider, _) {
              return IconButton(
                icon: Icon(
                  themeProvider.isDarkMode
                      ? Icons.light_mode
                      : Icons.dark_mode,
                ),
                onPressed: themeProvider.toggleTheme,
              );
            },
          ),
        ],
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: modules.length,
        itemBuilder: (context, index) {
          final module = modules[index];
          return Padding(
            padding: const EdgeInsets.only(bottom: 12),
            child: Card(
              child: InkWell(
                borderRadius: BorderRadius.circular(12),
                onTap: () => Navigator.pushNamed(context, module.route),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    children: [
                      Container(
                        width: 48,
                        height: 48,
                        decoration: BoxDecoration(
                          color: module.color.withValues(alpha: 0.15),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Icon(module.icon, color: module.color),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              module.title,
                              style: Theme.of(context)
                                  .textTheme
                                  .titleMedium
                                  ?.copyWith(fontWeight: FontWeight.bold),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              module.subtitle,
                              style: Theme.of(context).textTheme.bodySmall,
                            ),
                          ],
                        ),
                      ),
                      Icon(
                        Icons.chevron_right,
                        color: Theme.of(context).disabledColor,
                      ),
                    ],
                  ),
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}
