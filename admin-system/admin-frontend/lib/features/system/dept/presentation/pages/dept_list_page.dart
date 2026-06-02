import 'package:flutter/material.dart';

class DeptListPage extends StatelessWidget {
  const DeptListPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(child: Text('部门管理', style: Theme.of(context).textTheme.headlineSmall));
  }
}
