import 'package:flutter/material.dart';
import '../../app/routes/app_routes.dart';
import 'second_page.dart';
import 'third_page.dart';

class NavigationDemoPage extends StatelessWidget {
  const NavigationDemoPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('路由导航')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            child: ListTile(
              leading: const Icon(Icons.arrow_forward, color: Colors.blue),
              title: const Text('基本导航 push/pop'),
              subtitle: const Text('Navigator.push / Navigator.pop'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _navigateWithPush(context),
            ),
          ),
          const SizedBox(height: 8),
          Card(
            child: ListTile(
              leading: const Icon(Icons.label, color: Colors.green),
              title: const Text('命名路由导航'),
              subtitle: const Text('Navigator.pushNamed'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _navigateWithNamedRoute(context),
            ),
          ),
          const SizedBox(height: 8),
          Card(
            child: ListTile(
              leading: const Icon(Icons.send, color: Colors.orange),
              title: const Text('带参数导航'),
              subtitle: const Text('传递数据到下一个页面'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _navigateWithArgs(context),
            ),
          ),
          const SizedBox(height: 8),
          Card(
            child: ListTile(
              leading: const Icon(Icons.back_hand, color: Colors.purple),
              title: const Text('带返回值导航'),
              subtitle: const Text('接收下一页面返回的数据'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () => _navigateWithResult(context),
            ),
          ),
          const SizedBox(height: 16),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Flutter 导航核心概念',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                  ),
                  const Divider(height: 24),
                  _NavConceptRow(
                    num: '1',
                    title: 'Navigator',
                    desc: '路由栈管理器，push 入栈 / pop 出栈',
                  ),
                  _NavConceptRow(
                    num: '2',
                    title: 'Route',
                    desc: '页面抽象，MaterialPageRoute 最常用',
                  ),
                  _NavConceptRow(
                    num: '3',
                    title: '命名路由',
                    desc: '在 MaterialApp 中统一注册路由表',
                  ),
                  _NavConceptRow(
                    num: '4',
                    title: '参数传递',
                    desc: 'arguments 传参 / 构造函数传参',
                  ),
                  _NavConceptRow(
                    num: '5',
                    title: '返回值',
                    desc: 'await push() 获取 pop() 返回的数据',
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _navigateWithPush(BuildContext context) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => const SecondPage(
          title: 'Push 导航页面',
          message: '通过 Navigator.push 跳转',
        ),
      ),
    );
  }

  void _navigateWithNamedRoute(BuildContext context) {
    Navigator.pushNamed(context, AppRoutes.secondPage);
  }

  void _navigateWithArgs(BuildContext context) {
    Navigator.pushNamed(
      context,
      AppRoutes.secondPage,
      arguments: {
        'title': '带参数的页面',
        'message': '这是通过 arguments 传递的数据',
      },
    );
  }

  Future<void> _navigateWithResult(BuildContext context) async {
    final result = await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => const ThirdPage(),
      ),
    );
    if (result != null && context.mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('返回值: $result')),
      );
    }
  }
}

class _NavConceptRow extends StatelessWidget {
  final String num;
  final String title;
  final String desc;

  const _NavConceptRow({
    required this.num,
    required this.title,
    required this.desc,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(
            radius: 14,
            backgroundColor: Theme.of(context).colorScheme.primaryContainer,
            child: Text(num,
                style: TextStyle(
                  fontSize: 12,
                  color: Theme.of(context).colorScheme.primary,
                )),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
                Text(desc,
                    style: TextStyle(
                      fontSize: 13,
                      color: Theme.of(context).disabledColor,
                    )),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
